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

DEFAULT_MONGO_URI = os.getenv(
    "MONGODB_URI",
    "mongodb+srv://psb8013_db_user:<db_password>@jetbrains.tzw6r2y.mongodb.net/?appName=Jetbrains"
)


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
    # Check if password is still placeholder
    if "<db_password>" in uri:
        env_pw = os.getenv("MONGODB_PASSWORD")
        if env_pw:
            uri = uri.replace("<db_password>", env_pw)
        else:
            logging.info("MongoDB password is placeholder. Set MONGODB_PASSWORD env var to sync to live Atlas cluster. Skipping remote sync.")
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
