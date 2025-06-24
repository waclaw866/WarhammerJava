# Warhammer Fantasy 2e Encounter Manager

A dark, atmospheric web application for managing encounters in Warhammer Fantasy Roleplay 2nd Edition. Built with React frontend, FastAPI backend, and JSON-based data storage for easy editing and sharing.

## 🎯 Features

- **Enemy Management**: Browse and manage creatures with full Warhammer Fantasy 2e stat blocks
- **Weapon Arsenal**: Complete weapon database with damage and traits
- **Dice Rolling**: Integrated dice roller for d100, d10, and d6 rolls
- **JSON-Based Storage**: Easy-to-edit JSON files for enemies and weapons
- **Dark Warhammer Theme**: Atmospheric UI with castle backgrounds and medieval styling

## 🏗️ Architecture

- **Frontend**: React application with dark Warhammer theming
- **Backend**: FastAPI with JSON file-based data storage  
- **Data**: `/app/data/enemies.json` and `/app/data/weapons.json`
- **No Database Required**: All data stored in easily editable JSON files

## 🚀 Running the Application

The application is already configured and running:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8001
- **API Documentation**: http://localhost:8001/docs

## 📊 Default Data

### Enemies (Bestiary)
- **Goblin**: Classic cowardly greenskin with basic weapons
- **Orc**: Brutal warrior with enhanced strength
- **Skaven Clanrat**: Agile ratman with scurry ability

### Weapons (Armoury)  
- **Short Sword**: One-handed piercing weapon
- **Spear**: Two-handed reach weapon
- **Crossbow**: Ranged weapon with reload
- **Club**: Basic crude weapon
- **Hand Weapon**: Standard one-handed weapon

## 🎲 Warhammer Fantasy 2e Stats

Each enemy includes the full stat block:
- **WS** (Weapon Skill)
- **BS** (Ballistic Skill) 
- **S** (Strength)
- **T** (Toughness)
- **Ag** (Agility)
- **Int** (Intelligence)
- **WP** (Will Power)
- **Fel** (Fellowship)
- **A** (Attacks)
- **W** (Wounds)

## 📝 Editing Data

### Adding New Enemies
Edit `/app/data/enemies.json`:

```json
{
  "id": "new-enemy",
  "name": "New Enemy",
  "stats": {
    "weaponSkill": 4,
    "ballisticSkill": 3,
    "strength": 4,
    "toughness": 3,
    "agility": 3,
    "intelligence": 2,
    "willPower": 3,
    "fellowship": 2,
    "attacks": 1,
    "wounds": 2
  },
  "abilities": [
    {
      "name": "Special Ability",
      "description": "Description of the ability"
    }
  ],
  "weaponName": "Hand Weapon"
}
```

### Adding New Weapons
Edit `/app/data/weapons.json`:

```json
{
  "id": "new-weapon",
  "name": "New Weapon",
  "damage": 2,
  "traits": "two-handed, special"
}
```

## 🔧 API Endpoints

- `GET /api/enemies` - Get all enemies
- `POST /api/enemies` - Create new enemy
- `PUT /api/enemies/{id}` - Update enemy
- `DELETE /api/enemies/{id}` - Delete enemy
- `GET /api/weapons` - Get all weapons
- `POST /api/roll-d100` - Roll d100
- `POST /api/roll-d10` - Roll d10  
- `POST /api/roll-d6` - Roll d6

## 🎨 UI Features

### Three Main Tabs:
1. **Bestiary**: Browse and manage enemies with full stat blocks
2. **Armoury**: View available weapons with damage and traits
3. **Encounter**: Dice rolling interface for game sessions

### Visual Elements:
- Dark medieval theme with atmospheric backgrounds
- Enemy cards with full stat blocks and abilities
- Weapon information with damage and traits
- Animated dice roll notifications
- Delete functionality for managing encounters

## 🧪 Testing

Backend API has been fully tested and verified:
- All CRUD operations working
- Dice rolling functional
- JSON data persistence confirmed
- Proper Warhammer stat block handling

## 🎯 Usage

1. **Browse Enemies**: Use the Bestiary tab to view available creatures
2. **Check Weapons**: Review weapon stats in the Armoury tab  
3. **Roll Dice**: Use the Encounter tab or enemy card buttons for dice rolls
4. **Edit Data**: Modify JSON files directly for custom content
5. **Delete Enemies**: Use the X button on enemy cards to remove them

Perfect for Warhammer Fantasy 2e Game Masters who want a streamlined, atmospheric tool for managing encounters with easy customization through JSON files.
