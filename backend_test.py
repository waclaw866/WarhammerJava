#!/usr/bin/env python3
"""
Warhammer Fantasy 2e Encounter Manager - Backend API Tests
"""

import requests
import json
import sys
from pprint import pprint

class WarhammerAPITester:
    def __init__(self, base_url="http://localhost:8001"):
        self.base_url = base_url
        self.tests_run = 0
        self.tests_passed = 0
        self.test_results = []

    def run_test(self, name, method, endpoint, expected_status=200, data=None, expected_data=None):
        """Run a single API test"""
        url = f"{self.base_url}{endpoint}"
        headers = {'Content-Type': 'application/json'}
        
        self.tests_run += 1
        print(f"\n🔍 Testing {name}...")
        
        try:
            if method == 'GET':
                response = requests.get(url, headers=headers)
            elif method == 'POST':
                response = requests.post(url, json=data, headers=headers)
            elif method == 'DELETE':
                response = requests.delete(url, headers=headers)
            elif method == 'PUT':
                response = requests.put(url, json=data, headers=headers)
            
            status_success = response.status_code == expected_status
            
            if status_success:
                print(f"✅ Status: {response.status_code} (Expected: {expected_status})")
                
                # For successful responses, check the data if expected_data is provided
                if expected_data and response.status_code == 200:
                    response_data = response.json()
                    data_success = self._validate_data(response_data, expected_data)
                    
                    if data_success:
                        print("✅ Response data validation passed")
                        self.tests_passed += 1
                        result = {"name": name, "status": "PASS", "details": ""}
                    else:
                        print("❌ Response data validation failed")
                        result = {"name": name, "status": "FAIL", "details": "Data validation failed"}
                else:
                    self.tests_passed += 1
                    result = {"name": name, "status": "PASS", "details": ""}
            else:
                print(f"❌ Status: {response.status_code} (Expected: {expected_status})")
                result = {"name": name, "status": "FAIL", "details": f"Expected status {expected_status}, got {response.status_code}"}
            
            self.test_results.append(result)
            return response
            
        except Exception as e:
            print(f"❌ Error: {str(e)}")
            self.test_results.append({"name": name, "status": "FAIL", "details": str(e)})
            return None

    def _validate_data(self, actual, expected):
        """Validate that the actual data contains the expected data"""
        if isinstance(expected, list) and isinstance(actual, list):
            # For lists, check if all expected items are in the actual list
            if len(expected) == 0:
                return True
            
            # If expected is a list with specific items, check if each expected item
            # has a matching item in the actual list
            for exp_item in expected:
                found = False
                if isinstance(exp_item, dict) and "name" in exp_item:
                    # For dictionaries with a name field, just check if the name exists
                    for act_item in actual:
                        if isinstance(act_item, dict) and "name" in act_item:
                            if exp_item["name"] == act_item["name"]:
                                found = True
                                break
                else:
                    # For other items, check for an exact match
                    for act_item in actual:
                        if self._item_matches(act_item, exp_item):
                            found = True
                            break
                
                if not found:
                    print(f"❌ Expected item not found: {exp_item}")
                    return False
            return True
            
        elif isinstance(expected, dict) and isinstance(actual, dict):
            # For dictionaries, check if all expected keys and values are in the actual dict
            for key, value in expected.items():
                if key not in actual:
                    print(f"❌ Expected key '{key}' not found")
                    return False
                if not self._item_matches(actual[key], value):
                    print(f"❌ Value mismatch for key '{key}': expected {value}, got {actual[key]}")
                    return False
            return True
            
        else:
            # For simple values, check equality
            return actual == expected

    def _item_matches(self, actual, expected):
        """Check if an item matches the expected value, recursively for dicts and lists"""
        if isinstance(expected, dict) and isinstance(actual, dict):
            return self._validate_data(actual, expected)
        elif isinstance(expected, list) and isinstance(actual, list):
            return self._validate_data(actual, expected)
        else:
            return actual == expected

    def print_summary(self):
        """Print a summary of all test results"""
        print("\n" + "="*50)
        print(f"📊 TEST SUMMARY: {self.tests_passed}/{self.tests_run} tests passed")
        print("="*50)
        
        for result in self.test_results:
            status_icon = "✅" if result["status"] == "PASS" else "❌"
            print(f"{status_icon} {result['name']}")
            if result["status"] == "FAIL" and result["details"]:
                print(f"   Details: {result['details']}")
        
        print("="*50)
        return self.tests_passed == self.tests_run

def main():
    # Get the backend URL from environment or use default
    backend_url = "http://10.64.133.7:8001"
    
    # Create the tester
    tester = WarhammerAPITester(backend_url)
    
    # Test health endpoint
    tester.run_test(
        "Health Check", 
        "GET", 
        "/api/health",
        expected_data={"status": "healthy"}
    )
    
    # Test getting enemies
    enemies_response = tester.run_test(
        "Get Enemies", 
        "GET", 
        "/api/enemies",
        expected_data=[
            {"name": "Goblin"},
            {"name": "Orc"},
            {"name": "Skaven Clanrat"}
        ]
    )
    
    # Test getting weapons
    weapons_response = tester.run_test(
        "Get Weapons", 
        "GET", 
        "/api/weapons",
        expected_data=[
            {"name": "Short Sword"},
            {"name": "Spear"},
            {"name": "Crossbow"},
            {"name": "Club"},
            {"name": "Hand Weapon"}
        ]
    )
    
    # Test dice rolling endpoints
    tester.run_test("Roll d100", "POST", "/api/roll-d100")
    tester.run_test("Roll d10", "POST", "/api/roll-d10")
    tester.run_test("Roll d6", "POST", "/api/roll-d6")
    
    # Test enemy deletion (if we have enemies from the previous test)
    if enemies_response and enemies_response.status_code == 200:
        enemies = enemies_response.json()
        if enemies and len(enemies) > 0:
            # Get the first enemy ID
            enemy_id = enemies[0]["id"]
            
            # Test deleting an enemy
            tester.run_test(
                "Delete Enemy", 
                "DELETE", 
                f"/api/enemies/{enemy_id}"
            )
            
            # Verify the enemy was deleted
            tester.run_test(
                "Verify Enemy Deleted", 
                "GET", 
                "/api/enemies",
                expected_data=[
                    {"name": enemy["name"]} for enemy in enemies[1:]
                ]
            )
    
    # Print summary
    success = tester.print_summary()
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())