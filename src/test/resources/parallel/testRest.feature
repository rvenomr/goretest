@functional
Feature: User Scenarios

  Scenario Outline: GET User
    Given we want to verify '<type>' responses
    When send GET request
    When verify that operation response code is 200
    When send GET for user with 'name=Tenali'
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: GET User by email
    Given we want to verify '<type>' responses
    When send GET for user with 'email=tenali'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: GET User by gender
    Given we want to verify '<type>' responses
    When send GET for user with 'gender=male'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: GET User by operation response
    Given we want to verify '<type>' responses
    When send GET for user with 'operation response=active'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: GET User
    Given we want to verify '<type>' responses
    When send GET for user with 'operation response=inactive'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |


  Scenario Outline: GET User posts
    Given we want to verify '<type>' responses
    When send GET request to path '/public-api/users/222/posts'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: GET User todos
    Given we want to verify '<type>' responses
    When send GET request to path '/public-api/users/222/todos'
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying POST
    Given we want to verify '<type>' responses
    When send POST
    When verify that operation response code is 201
    When send GET with previously received user ID
    When verify that operation response code is 200
    When verify that response contains user
    When send POST with illformated data
    When send 'PATCH' with corrupted body
    When verify that operation response code is 400
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying PUT
    Given we want to verify '<type>' responses
    When send POST
    When verify that operation response code is 201
    Then send PUT
    When verify that operation response code is 200
    Then send PUT
    When verify that operation response code is 200
    When send GET with previously received user ID
    When verify that operation response code is 200
    When send 'PUT' with illformated data
    When send GET with previously received user ID
    When verify that operation response code is 200
    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying PATCH
    Given we want to verify '<type>' responses
    When send POST
    When verify that operation response code is 201
    Then send PATCH
    When verify that operation response code is 200
    Then send PATCH
    When verify that operation response code is 200
    When send GET with previously received user ID
    When verify that operation response code is 200
    When send 'PATCH' with illformated data
    When send GET with previously received user ID
    When verify that operation response code is 200
    When send 'PATCH' with corrupted body
    When verify that operation response code is 400

    Examples:
      | type |
      | xml  |
      | json |

  Scenario Outline: Verifying DELETE
    Given we want to verify '<type>' responses
    When send POST
    When send DELETE with previously received user ID
    When verify that operation response code is 204
    When send GET with previously received user ID
    When verify that operation response code is 404
    Examples:
      | type |
      | xml  |
      | json |