import React, { useState, useEffect } from 'react';
import './App.css';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8001';

function App() {
  const [enemies, setEnemies] = useState([]);
  const [weapons, setWeapons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('enemies');
  const [selectedEnemy, setSelectedEnemy] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [diceRoll, setDiceRoll] = useState(null);

  // Load data on component mount
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [enemiesRes, weaponsRes] = await Promise.all([
        fetch(`${BACKEND_URL}/api/enemies`),
        fetch(`${BACKEND_URL}/api/weapons`)
      ]);
      
      const enemiesData = await enemiesRes.json();
      const weaponsData = await weaponsRes.json();
      
      setEnemies(enemiesData);
      setWeapons(weaponsData);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const rollDice = async (diceType) => {
    try {
      const response = await fetch(`${BACKEND_URL}/api/roll-d${diceType}`, {
        method: 'POST'
      });
      const result = await response.json();
      setDiceRoll({ type: diceType, result: result.roll });
      setTimeout(() => setDiceRoll(null), 3000);
    } catch (error) {
      console.error('Error rolling dice:', error);
    }
  };

  const deleteEnemy = async (enemyId) => {
    try {
      await fetch(`${BACKEND_URL}/api/enemies/${enemyId}`, {
        method: 'DELETE'
      });
      await loadData();
    } catch (error) {
      console.error('Error deleting enemy:', error);
    }
  };

  const StatBlock = ({ stats }) => (
    <div className="stat-block">
      <div className="stat-grid">
        <div className="stat-item">
          <span className="stat-label">WS</span>
          <span className="stat-value">{stats.weapon_skill || stats.weaponSkill}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">BS</span>
          <span className="stat-value">{stats.ballistic_skill || stats.ballisticSkill}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">S</span>
          <span className="stat-value">{stats.strength}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">T</span>
          <span className="stat-value">{stats.toughness}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Ag</span>
          <span className="stat-value">{stats.agility}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Int</span>
          <span className="stat-value">{stats.intelligence}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">WP</span>
          <span className="stat-value">{stats.will_power || stats.willPower}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Fel</span>
          <span className="stat-value">{stats.fellowship}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">A</span>
          <span className="stat-value">{stats.attacks}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">W</span>
          <span className="stat-value">{stats.wounds}</span>
        </div>
      </div>
    </div>
  );

  const EnemyCard = ({ enemy }) => {
    const weapon = weapons.find(w => w.name === (enemy.weapon_name || enemy.weaponName));
    
    return (
      <div className="enemy-card">
        <div className="enemy-header">
          <h3 className="enemy-name">{enemy.name}</h3>
          <button 
            className="delete-btn"
            onClick={() => deleteEnemy(enemy.id)}
            title="Delete Enemy"
          >
            ×
          </button>
        </div>
        
        <StatBlock stats={enemy.stats} />
        
        {weapon && (
          <div className="weapon-info">
            <h4>Weapon: {weapon.name}</h4>
            <p>Damage: {weapon.damage} | Traits: {weapon.traits}</p>
          </div>
        )}
        
        {enemy.abilities && enemy.abilities.length > 0 && (
          <div className="abilities">
            <h4>Abilities:</h4>
            {enemy.abilities.map((ability, index) => (
              <div key={index} className="ability">
                <strong>{ability.name}:</strong> {ability.description}
              </div>
            ))}
          </div>
        )}
        
        <div className="enemy-actions">
          <button className="action-btn" onClick={() => rollDice(100)}>
            Attack Roll (d100)
          </button>
          <button className="action-btn" onClick={() => rollDice(10)}>
            Damage (d10)
          </button>
        </div>
      </div>
    );
  };

  const WeaponCard = ({ weapon }) => (
    <div className="weapon-card">
      <h3 className="weapon-name">{weapon.name}</h3>
      <div className="weapon-stats">
        <p><strong>Damage:</strong> {weapon.damage}</p>
        <p><strong>Traits:</strong> {weapon.traits}</p>
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="app">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading the Old World...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      {/* Header */}
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">Warhammer Fantasy 2e</h1>
          <h2 className="app-subtitle">Encounter Manager</h2>
        </div>
        <div className="header-background"></div>
      </header>

      {/* Dice Roll Notification */}
      {diceRoll && (
        <div className="dice-notification">
          <div className="dice-result">
            <span className="dice-type">d{diceRoll.type}</span>
            <span className="dice-value">{diceRoll.result}</span>
          </div>
        </div>
      )}

      {/* Main Content */}
      <main className="main-content">
        {/* Navigation Tabs */}
        <nav className="tab-nav">
          <button 
            className={`tab-btn ${activeTab === 'enemies' ? 'active' : ''}`}
            onClick={() => setActiveTab('enemies')}
          >
            <span className="tab-icon">⚔️</span>
            Bestiary
          </button>
          <button 
            className={`tab-btn ${activeTab === 'weapons' ? 'active' : ''}`}
            onClick={() => setActiveTab('weapons')}
          >
            <span className="tab-icon">🗡️</span>
            Armoury
          </button>
          <button 
            className={`tab-btn ${activeTab === 'encounter' ? 'active' : ''}`}
            onClick={() => setActiveTab('encounter')}
          >
            <span className="tab-icon">🎲</span>
            Encounter
          </button>
        </nav>

        {/* Content Areas */}
        <div className="content-area">
          {activeTab === 'enemies' && (
            <div className="enemies-section">
              <div className="section-header">
                <h2>Bestiary</h2>
                <p className="section-subtitle">Creatures of the Old World</p>
              </div>
              <div className="cards-grid">
                {enemies.map(enemy => (
                  <EnemyCard key={enemy.id} enemy={enemy} />
                ))}
              </div>
            </div>
          )}

          {activeTab === 'weapons' && (
            <div className="weapons-section">
              <div className="section-header">
                <h2>Armoury</h2>
                <p className="section-subtitle">Tools of War</p>
              </div>
              <div className="cards-grid">
                {weapons.map(weapon => (
                  <WeaponCard key={weapon.id} weapon={weapon} />
                ))}
              </div>
            </div>
          )}

          {activeTab === 'encounter' && (
            <div className="encounter-section">
              <div className="section-header">
                <h2>Encounter Manager</h2>
                <p className="section-subtitle">Manage your battles</p>
              </div>
              <div className="dice-section">
                <h3>Dice Roller</h3>
                <div className="dice-buttons">
                  <button className="dice-btn" onClick={() => rollDice(100)}>
                    d100
                  </button>
                  <button className="dice-btn" onClick={() => rollDice(10)}>
                    d10
                  </button>
                  <button className="dice-btn" onClick={() => rollDice(6)}>
                    d6
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

export default App;