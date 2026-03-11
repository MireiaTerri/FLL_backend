Feature: Admin Authorization
	In order to protect system data
	As a system
	I want to ensure only administrators can modify resources while anyone can read

	Scenario: Anonymous user can read editions
		Given I'm not logged in
		And There is a persisted edition with year 2025, venue "Barcelona" and description "FLL 2025"
		When I retrieve the persisted edition
		Then The response code is 200

	Scenario: Anonymous user cannot create an edition
		Given I'm not logged in
		When I create a new edition with year 2026, venue "Madrid" and description "FLL 2026"
		Then The response code is 401

	Scenario: Regular user cannot create an edition
		Given There is a registered user with username "regularuser" and password "password" and email "regular@sample.app"
		And I login as "regularuser" with password "password"
		When I create a new edition with year 2026, venue "Madrid" and description "FLL 2026"
		Then The response code is 403

	Scenario: Administrator can create an edition
		Given I login as "admin" with password "password"
		When I create a new edition with year 2026, venue "Madrid" and description "FLL 2026"
		Then The response code is 201

	Scenario: Anonymous user can read venues
		Given I'm not logged in
		And There is a persisted venue with name "Test Venue" and city "Lleida"
		When I retrieve the persisted venue
		Then The response code is 200

	Scenario: Regular user cannot create a venue
		Given There is a registered user with username "regularuser" and password "password" and email "regular@sample.app"
		And I login as "regularuser" with password "password"
		When I create a new venue with name "Forbidden Venue" and city "Nowhere"
		Then The response code is 403

	Scenario: Administrator can create a venue
		Given I login as "admin" with password "password"
		When I create a new venue with name "Admin Venue" and city "Barcelona"
		Then The response code is 201

