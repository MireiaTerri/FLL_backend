Feature: Login
	In order to access the system
	As a user
	I want to authenticate and verify my identity

	Scenario: Login as administrator
		Given I login as "admin" with password "password"
		When I retrieve my identity
		Then The response code is 200
		And The identity username is "admin"

	Scenario: Login with wrong password
		Given I login as "admin" with password "wrongpassword"
		When I retrieve my identity
		Then The response code is 401

	Scenario: Login as non-existent user
		Given I login as "ghost" with password "password"
		When I retrieve my identity
		Then The response code is 401

	Scenario: Anonymous access to identity is rejected
		Given I'm not logged in
		When I retrieve my identity
		Then The response code is 401

	Scenario: Login as registered regular user
		Given There is a registered user with username "regularuser" and password "password" and email "regular@sample.app"
		And I login as "regularuser" with password "password"
		When I retrieve my identity
		Then The response code is 200
		And The identity username is "regularuser"

