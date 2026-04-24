#!/usr/bin/env python3
"""
Convert OSM (OpenStreetMap) XML file to JSON format
"""

import xml.etree.ElementTree as ET
import json
from pathlib import Path

def osm_to_json(osm_file_path, json_file_path):
    """
    Convert OSM XML file to JSON format

    Args:
        osm_file_path: Path to the OSM file
        json_file_path: Path where the JSON file will be saved
    """

    print(f"Reading OSM file: {osm_file_path}")

    # Parse the XML file
    tree = ET.parse(osm_file_path)
    root = tree.getroot()

    # Extract metadata
    meta = root.find('meta')
    meta_dict = meta.attrib if meta is not None else {}

    # Extract notes
    note = root.find('note')
    note_text = note.text if note is not None else None

    # Build the JSON structure
    osm_data = {
        "version": root.attrib.get("version"),
        "generator": root.attrib.get("generator"),
        "note": note_text,
        "meta": meta_dict,
        "nodes": [],
        "ways": [],
        "relations": []
    }

    # Extract nodes
    print("Extracting nodes...")
    node_count = 0
    for node in root.findall('node'):
        node_dict = {
            "id": int(node.attrib.get("id")),
            "lat": float(node.attrib.get("lat")),
            "lon": float(node.attrib.get("lon"))
        }

        # Add optional tags
        tags = {}
        for tag in node.findall('tag'):
            k = tag.attrib.get('k')
            v = tag.attrib.get('v')
            if k and v:
                tags[k] = v
        if tags:
            node_dict["tags"] = tags

        osm_data["nodes"].append(node_dict)
        node_count += 1
        if node_count % 100000 == 0:
            print(f"  Processed {node_count} nodes...")

    # Extract ways
    print("Extracting ways...")
    way_count = 0
    for way in root.findall('way'):
        way_dict = {
            "id": int(way.attrib.get("id")),
            "nodes": []
        }

        # Extract node references
        for nd in way.findall('nd'):
            way_dict["nodes"].append(int(nd.attrib.get("ref")))

        # Add tags
        tags = {}
        for tag in way.findall('tag'):
            k = tag.attrib.get('k')
            v = tag.attrib.get('v')
            if k and v:
                tags[k] = v
        if tags:
            way_dict["tags"] = tags

        osm_data["ways"].append(way_dict)
        way_count += 1
        if way_count % 10000 == 0:
            print(f"  Processed {way_count} ways...")

    # Extract relations
    print("Extracting relations...")
    relation_count = 0
    for relation in root.findall('relation'):
        relation_dict = {
            "id": int(relation.attrib.get("id")),
            "members": []
        }

        # Extract members
        for member in relation.findall('member'):
            member_dict = {
                "type": member.attrib.get("type"),
                "ref": int(member.attrib.get("ref")),
                "role": member.attrib.get("role", "")
            }
            relation_dict["members"].append(member_dict)

        # Add tags
        tags = {}
        for tag in relation.findall('tag'):
            k = tag.attrib.get('k')
            v = tag.attrib.get('v')
            if k and v:
                tags[k] = v
        if tags:
            relation_dict["tags"] = tags

        osm_data["relations"].append(relation_dict)
        relation_count += 1
        if relation_count % 1000 == 0:
            print(f"  Processed {relation_count} relations...")

    # Write to JSON file
    print(f"\nWriting JSON file: {json_file_path}")
    with open(json_file_path, 'w', encoding='utf-8') as f:
        json.dump(osm_data, f, indent=2)

    print(f"\nConversion complete!")
    print(f"Nodes: {len(osm_data['nodes'])}")
    print(f"Ways: {len(osm_data['ways'])}")
    print(f"Relations: {len(osm_data['relations'])}")

if __name__ == "__main__":
    osm_file = Path("src/test/resources/osmData/Bornholm.osm")
    json_file = Path("src/test/resources/jsonData/Bornholm.json")

    osm_to_json(str(osm_file), str(json_file))

