@functional
Feature: User Scenarios

  Scenario Outline: GET
    Given we want to verify '<type>' responses
    When send GET request
    When verify that status code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying POST
    Given we want to verify '<type>' responses
    When send POST
    When verify that status code is 201
    When send GET with previously received user ID
    When verify that status code is 200
    When verify that response contains user
    When send POST with illformated data
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying PUT
    Given we want to verify '<type>' responses
    When send POST
    When verify that status code is 201
    Then send PUT
    When verify that status code is 200
    Then send PUT
    When verify that status code is 200
    When send GET with previously received user ID
    When verify that status code is 200
    When send PUT with illformated data
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying DELETE
    Given we want to verify '<type>' responses
    When send POST
    When send DELETE with previously received user ID
    When verify that status code is 204
    When send GET with previously received user ID
    When verify that status code is 404
    Examples:
      | type |
      | xml  |
      | json |