@functional
Feature: User Scenarios

  Scenario Outline: GET
    Given we want to verify '<type>' requests and responses
    When I send GET request
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: POST
    Given we want to verify '<type>' requests and responses
    When I send POST
    When I send GET with previously received user ID
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: PUT
    Given we want to verify '<type>' requests and responses
    When I send POST
    Then I send PUT
    Examples:
      | type |
      | xml  |
      | json |