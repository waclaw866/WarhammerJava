#!/usr/bin/env python3
"""
Warhammer Fantasy 2e Encounter Manager - Backend
JSON-based data storage for enemies and weapons
"""

import os
import json
import uuid
from typing import List, Dict, Optional, Any
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from pathlib import Path

# Initialize FastAPI app
app = FastAPI(title="Warhammer Fantasy 2e Encounter Manager", version="1.0.0")

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Data directory
DATA_DIR = Path("/app/data")
DATA_DIR.mkdir(exist_ok=True)

ENEMIES_FILE = DATA_DIR / "enemies.json"
WEAPONS_FILE = DATA_DIR / "weapons.json"

# Pydantic models for Warhammer Fantasy 2e
class StatBlock(BaseModel):
    weapon_skill: int = Field(alias="weaponSkill", default=0)
    ballistic_skill: int = Field(alias="ballisticSkill", default=0)
    strength: int = Field(alias="strength", default=0)
    toughness: int = Field(alias="toughness", default=0)
    agility: int = Field(alias="agility", default=0)
    intelligence: int = Field(alias="intelligence", default=0)
    will_power: int = Field(alias="willPower", default=0)
    fellowship: int = Field(alias="fellowship", default=0)
    attacks: int = Field(alias="attacks", default=1)
    wounds: int = Field(alias="wounds", default=1)
    
    class Config:
        allow_population_by_field_name = True

class Ability(BaseModel):
    name: str
    description: str

class Weapon(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    name: str
    damage: int
    traits: str = ""
    
class Enemy(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    name: str
    stats: StatBlock
    abilities: List[Ability] = []
    weapon_name: str = Field(alias="weaponName", default="")
    current_wounds: int = Field(alias="currentWounds", default=0)
    
    class Config:
        allow_population_by_field_name = True
    
    def __init__(self, **data):
        super().__init__(**data)
        if self.current_wounds == 0:
            self.current_wounds = self.stats.wounds

class EncounterEnemy(BaseModel):
    enemy_id: str
    name: str
    current_wounds: int
    max_wounds: int
    initiative: int = 0
    notes: str = ""

# Data management functions
def load_json_data(file_path: Path, default_data: List = None) -> List[Dict]:
    """Load JSON data from file, create with default if doesn't exist"""
    if default_data is None:
        default_data = []
    
    if not file_path.exists():
        with open(file_path, 'w') as f:
            json.dump(default_data, f, indent=2)
        return default_data
    
    try:
        with open(file_path, 'r') as f:
            return json.load(f)
    except (json.JSONDecodeError, FileNotFoundError):
        return default_data

def save_json_data(file_path: Path, data: List[Dict]):
    """Save data to JSON file"""
    with open(file_path, 'w') as f:
        json.dump(data, f, indent=2)

def get_default_weapons() -> List[Dict]:
    """Default Warhammer weapons"""
    return [
        {
            "id": "short-sword",
            "name": "Short Sword",
            "damage": 1,
            "traits": "one-handed, piercing"
        },
        {
            "id": "spear",
            "name": "Spear",
            "damage": 1,
            "traits": "two-handed, reach"
        },
        {
            "id": "crossbow",
            "name": "Crossbow",
            "damage": 2,
            "traits": "ranged, reload"
        },
        {
            "id": "club",
            "name": "Club",
            "damage": 0,
            "traits": "one-handed, crude"
        },
        {
            "id": "hand-weapon",
            "name": "Hand Weapon",
            "damage": 1,
            "traits": "one-handed"
        }
    ]

def get_default_enemies() -> List[Dict]:
    """Default Warhammer enemies"""
    return [
        {
            "id": "goblin",
            "name": "Goblin",
            "stats": {
                "weaponSkill": 3,
                "ballisticSkill": 3,
                "strength": 3,
                "toughness": 3,
                "agility": 3,
                "intelligence": 2,
                "willPower": 2,
                "fellowship": 2,
                "attacks": 1,
                "wounds": 1
            },
            "abilities": [
                {
                    "name": "Cowardly",
                    "description": "Must pass Will Power test to stand and fight if outnumbered"
                }
            ],
            "weaponName": "Short Sword"
        },
        {
            "id": "orc",
            "name": "Orc",
            "stats": {
                "weaponSkill": 4,
                "ballisticSkill": 3,
                "strength": 4,
                "toughness": 4,
                "agility": 2,
                "intelligence": 2,
                "willPower": 3,
                "fellowship": 2,
                "attacks": 1,
                "wounds": 2
            },
            "abilities": [
                {
                    "name": "Brutal",
                    "description": "+1 damage on critical hits"
                }
            ],
            "weaponName": "Hand Weapon"
        },
        {
            "id": "skaven-clanrat",
            "name": "Skaven Clanrat",
            "stats": {
                "weaponSkill": 3,
                "ballisticSkill": 3,
                "strength": 3,
                "toughness": 3,
                "agility": 4,
                "intelligence": 3,
                "willPower": 2,
                "fellowship": 2,
                "attacks": 1,
                "wounds": 1
            },
            "abilities": [
                {
                    "name": "Scurry",
                    "description": "+1 to Initiative when fleeing"
                }
            ],
            "weaponName": "Spear"
        }
    ]

# Initialize data files
load_json_data(WEAPONS_FILE, get_default_weapons())
load_json_data(ENEMIES_FILE, get_default_enemies())

# API Routes
@app.get("/api/health")
async def health_check():
    return {"status": "healthy", "message": "Warhammer Fantasy 2e Encounter Manager"}

# Weapons endpoints
@app.get("/api/weapons")
async def get_weapons() -> List[Weapon]:
    data = load_json_data(WEAPONS_FILE, get_default_weapons())
    return [Weapon(**weapon) for weapon in data]

@app.post("/api/weapons")
async def create_weapon(weapon: Weapon) -> Weapon:
    data = load_json_data(WEAPONS_FILE, get_default_weapons())
    weapon_dict = weapon.dict()
    data.append(weapon_dict)
    save_json_data(WEAPONS_FILE, data)
    return weapon

@app.put("/api/weapons/{weapon_id}")
async def update_weapon(weapon_id: str, weapon: Weapon) -> Weapon:
    data = load_json_data(WEAPONS_FILE, get_default_weapons())
    for i, w in enumerate(data):
        if w.get("id") == weapon_id:
            weapon.id = weapon_id
            data[i] = weapon.dict()
            save_json_data(WEAPONS_FILE, data)
            return weapon
    raise HTTPException(status_code=404, detail="Weapon not found")

@app.delete("/api/weapons/{weapon_id}")
async def delete_weapon(weapon_id: str):
    data = load_json_data(WEAPONS_FILE, get_default_weapons())
    data = [w for w in data if w.get("id") != weapon_id]
    save_json_data(WEAPONS_FILE, data)
    return {"message": "Weapon deleted"}

# Enemies endpoints
@app.get("/api/enemies")
async def get_enemies() -> List[Enemy]:
    data = load_json_data(ENEMIES_FILE, get_default_enemies())
    return [Enemy(**enemy) for enemy in data]

@app.post("/api/enemies")
async def create_enemy(enemy: Enemy) -> Enemy:
    data = load_json_data(ENEMIES_FILE, get_default_enemies())
    enemy_dict = enemy.dict(by_alias=True)
    data.append(enemy_dict)
    save_json_data(ENEMIES_FILE, data)
    return enemy

@app.put("/api/enemies/{enemy_id}")
async def update_enemy(enemy_id: str, enemy: Enemy) -> Enemy:
    data = load_json_data(ENEMIES_FILE, get_default_enemies())
    for i, e in enumerate(data):
        if e.get("id") == enemy_id:
            enemy.id = enemy_id
            data[i] = enemy.dict(by_alias=True)
            save_json_data(ENEMIES_FILE, data)
            return enemy
    raise HTTPException(status_code=404, detail="Enemy not found")

@app.delete("/api/enemies/{enemy_id}")
async def delete_enemy(enemy_id: str):
    data = load_json_data(ENEMIES_FILE, get_default_enemies())
    data = [e for e in data if e.get("id") != enemy_id]
    save_json_data(ENEMIES_FILE, data)
    return {"message": "Enemy deleted"}

# Dice rolling endpoints
@app.post("/api/roll-d100")
async def roll_d100():
    import random
    roll = random.randint(1, 100)
    return {"roll": roll}

@app.post("/api/roll-d10")
async def roll_d10():
    import random
    roll = random.randint(1, 10)
    return {"roll": roll}

@app.post("/api/roll-d6")
async def roll_d6():
    import random
    roll = random.randint(1, 6)
    return {"roll": roll}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)