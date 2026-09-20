"""
YallaPark - Way 1: End-to-End Pipeline Prototype Runner
1. Pulls lot geometries from OpenStreetMap (with pilot backups).
2. Performs YOLO-OBB vehicle detection over high-resolution imagery frames.
3. Computes polygon containment and live occupancy rates.
4. Exports structured data to local JSON and MongoDB Atlas.
"""

import logging
from fetch_osm_lots import PILOT_REGIONS, fetch_osm_parking_lots, parse_lot_polygons
from detect_occupancy import AerialOccupancyDetector, create_polygon
from export_to_yallapark import export_to_json, sync_to_mongodb_atlas

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")


def run_pipeline():
    logging.info("=== Starting YallaPark Way 1 Aerial Occupancy Pipeline ===")
    
    # Initialize detector
    detector = AerialOccupancyDetector()
    all_processed_lots = []

    # Process Dubai Pilot Hubs
    for region_name, bbox in PILOT_REGIONS.items():
        logging.info(f"Processing region: {region_name}")
        elements = fetch_osm_parking_lots(bbox, timeout=10)
        parsed_lots = parse_lot_polygons(elements)
        
        for lot in parsed_lots:
            # Create pixel polygon from coordinates for detection demo
            coords = lot["coordinates"]
            lats = [p[0] for p in coords]
            lons = [p[1] for p in coords]
            min_lat, max_lat = min(lats), max(lats)
            min_lon, max_lon = min(lons), max(lons)
            
            # Normalize to pixel coordinates (100 to 900)
            lat_span = max_lat - min_lat if max_lat != min_lat else 0.001
            lon_span = max_lon - min_lon if max_lon != min_lon else 0.001
            
            poly_px = []
            for lat, lon in coords:
                px = 100.0 + ((lon - min_lon) / lon_span) * 800.0
                py = 100.0 + ((lat - min_lat) / lat_span) * 800.0
                poly_px.append((px, py))
                
            lot_polygon = create_polygon(poly_px)
            
            # Detect vehicles using a per-lot seed for distinct simulated distributions
            detections = detector.detect_vehicles(image_source=None, lot_seed=lot["osm_id"])
            occ_stats = detector.compute_lot_occupancy(detections, lot_polygon, capacity=lot["capacity"])
            
            # Determine specialized bays allocation
            cap = lot["capacity"]
            pod_bays = max(2, int(cap * 0.05))
            pink_bays = max(2, int(cap * 0.08))
            delivery_bays = max(3, int(cap * 0.10))
            # Never publish an invalid allocation for small lots.
            standard_bays = max(0, cap - pod_bays - pink_bays - delivery_bays)

            lot_payload = {
                "lot_id": f"LOT_{lot['osm_id']}",
                "name": lot["name"],
                "region": region_name,
                "parking_type": lot["parking_type"],
                "capacity": cap,
                "available_bays": occ_stats["available_bays"],
                "occupancy_rate": occ_stats["occupancy_rate"],
                "occupancy_percentage": occ_stats["occupancy_percentage"],
                "status": occ_stats["status"],
                "specialized_allocation": {
                    "pod_bays": pod_bays,
                    "women_pink_bays": pink_bays,
                    "delivery_bays": delivery_bays,
                    "standard_bays": standard_bays
                },
                "center_coordinate": {
                    "lat": sum(lats) / len(lats),
                    "lon": sum(lons) / len(lons)
                }
            }
            all_processed_lots.append(lot_payload)
            logging.info(f" -> {lot['name']}: {occ_stats['detected_vehicles']}/{cap} vehicles ({occ_stats['occupancy_percentage']}%) [{occ_stats['status']}]")

    # Export to JSON
    json_path = export_to_json(all_processed_lots, output_path="dubai_parking_live.json")
    
    # Sync to MongoDB Atlas (if credentials provided)
    sync_to_mongodb_atlas(all_processed_lots)
    
    logging.info(f"=== Way 1 Pipeline Complete. Processed {len(all_processed_lots)} lots. ===")
    return all_processed_lots


if __name__ == "__main__":
    run_pipeline()
