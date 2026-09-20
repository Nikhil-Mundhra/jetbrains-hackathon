"""
YallaPark - Data Exporter & MongoDB Atlas Bridge
Syncs Way 1 aerial vehicle detections and lot occupancies to MongoDB Atlas
and generates local JSON feeds for the Kotlin Multiplatform client.
"""

import json
import logging
import os
from datetime import datetime, timezone
from typing import Dict, List, Any

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

DEFAULT_MONGO_URI = os.getenv("MONGODB_URI", "")


def export_to_json(lots_data: List[Dict[str, Any]], output_path: str = "dubai_parking_live.json") -> str:
    """
    Saves live occupancy feed to a local JSON file for KMP consumption.
    """
    payload = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "source": "YallaPark-Way1-Aerial-Vision",
        "total_lots": len(lots_data),
        "lots": lots_data
    }
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)
    logging.info(f"Successfully exported {len(lots_data)} lots to {output_path}")
    return output_path


def sync_to_mongodb_atlas(lots_data: List[Dict[str, Any]], uri: str = DEFAULT_MONGO_URI) -> bool:
    """
    Upserts live lot occupancy data to MongoDB Atlas collection `parking_lots`.
    """
    if not uri:
        logging.info("MONGODB_URI is not set. Set it to a valid Atlas connection string to enable remote sync. Skipping.")
        return False

    try:
        from pymongo import MongoClient, UpdateOne
        logging.info("Connecting to MongoDB Atlas...")
        client = MongoClient(uri, serverSelectionTimeoutMS=5000)
        db = client["yallapark"]
        collection = db["parking_lots"]

        operations = []
        now_str = datetime.now(timezone.utc).isoformat()
        for lot in lots_data:
            lot_copy = dict(lot)
            lot_copy["updated_at"] = now_str
            operations.append(
                UpdateOne(
                    {"lot_id": lot["lot_id"]},
                    {"$set": lot_copy},
                    upsert=True
                )
            )

        if operations:
            result = collection.bulk_write(operations)
            logging.info(f"MongoDB Atlas updated: {result.upserted_count} upserted, {result.modified_count} modified.")
        return True
    except Exception as e:
        logging.warning(f"MongoDB Atlas sync skipped or failed: {e}")
        return False
