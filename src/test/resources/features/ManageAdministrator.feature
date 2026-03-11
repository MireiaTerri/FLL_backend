Feature: Manage Administrator
	In order to manage administrator accounts
	As an administrator
	I want to be able to create, retrieve, update and delete administrators

	Scenario: Create an administrator
		Given I login as "admin" with password "password"
		When I create a new administrator with username "newadmin", email "newadmin@sample.app" and password "securepass"
		Then The response code is 201
		And It has been created an administrator with username "newadmin" and email "newadmin@sample.app"
		And I can login with username "newadmin" and password "securepass"

	Scenario: Retrieve an administrator
		Given I login as "admin" with password "password"
		When I retrieve the administrator with username "admin"
		Then The response code is 200
		And The response contains administrator email "admin@sample.app"

	Scenario: Update an administrator email
		Given I login as "admin" with password "password"
		And There is an administrator with username "updatable" and email "old@sample.app" and password "securepass"
		When I update administrator "updatable" email to "new@sample.app"
		Then The response code is 200
		And The response contains administrator email "new@sample.app"

	Scenario: Delete an administrator
		Given I login as "admin" with password "password"
		And There is an administrator with username "deletable" and email "del@sample.app" and password "securepass"
		When I delete the administrator with username "deletable"
		Then The response code is 200
		And The administrator with username "deletable" does not exist

	Scenario: Non-admin user cannot create an administrator
		Given There is a registered user with username "regularuser" and password "password" and email "regular@sample.app"
		And I login as "regularuser" with password "password"
		When I create a new administrator with username "hacker", email "hacker@sample.app" and password "securepass"
		Then The response code is 403

	Scenario: Anonymous user cannot create an administrator
		Given I'm not logged in
		When I create a new administrator with username "hacker", email "hacker@sample.app" and password "securepass"
		Then The response code is 401

	Scenario: Non-admin user cannot delete an administrator
		Given There is a registered user with username "regularuser" and password "password" and email "regular@sample.app"
		And I login as "regularuser" with password "password"
		When I delete the administrator with username "admin"
		Then The response code is 403

