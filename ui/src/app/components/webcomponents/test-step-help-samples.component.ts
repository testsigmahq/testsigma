import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {StepDetailsDataMap} from "../../models/step-details-data-map.model";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {TestCase} from "../../models/test-case.model";

@Component({
  selector: 'app-test-step-help-samples',
  templateUrl: './test-step-help-samples.component.html',
  styles: []
})
export class TestStepHelpSamplesComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public sampleUseCases: any = [];
  public sampleUseCaseByApp: any = [];
  public selectedUseCase: any

  constructor() {
  }

  ngOnInit(): void {
    this.sampleUseCases = [
      {
        "web application": [
          {
            "title": "Verify Successful Login",
            "id": 1,
            "steps": [
              "Navigate to http://travel.testsigma.com/",
              "Click on Login Icon",
              "Click on Login SImply Travel",
              "Enter admin in the Username field",
              "Enter 12345 in the Password field",
              "Click on Login button",
              "Verify that the current page displays text SEARCH FLIGHTS"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Search flights by cities (Data Driven)",
            "id": 2,
            "steps": [
              "Navigate to http://travel.testsigma.com/login",
              "Enter admin in the Username field",
              "Enter 12345 in the Password field",
              "Click on Login button",
              "Wait for 5 seconds",
              "Click on From Location",
              "Wait for 2 seconds",
              "Verify that the element From Location Dropdown is displayed",
              "Click on the element with text @|city|",
              "Click on To Location",
              "Verify that the element To Location Dropdown is displayed",
              "Click on To Location Dropdown List Item",
              "Click on Depart Datepicker",
              "Verify that the element Datepicker is displayed",
              "Wait until the element Today in Datepicker is visible",
              "Click on Today in Datepicker",
              "Click on Ok button",
              "Click on Search Flights button",
              "Verify that the current page displays text BO | Bo-747"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify flight booking (Using reusable Step Group)",
            "id": 3,
            "steps": [
              "Login step group (Reusable Steps)",
              "Click on From Location",
              "Wait for 2 seconds",
              "Verify that the element From City Dropdown is displayed",
              "Click on the element with text @|city|",
              "Click on To City",
              "Verify that the element To Location Dropdown is displayed",
              "Click on To Location Dropdown List Item",
              "Click on Depart Datepicker",
              "Verify that the element Datepicker is displayed",
              "Wait until the element Today in Datepicker is visible",
              "Click on Today in Datepicker",
              "Click on Ok button",
              "Click on Search Flights button",
              "Verify that the current page displays text BO | Bo-747",
              "Click on Book Flight button",
              "Scroll down to the element Review & Continue button into view",
              "Click on Review & Continue button",
              "Enter !|PhoneNumber - cellPhone()| in the Contact Mobile Number field",
              "Enter !|Internet - emailAddress()| in the Contact Email field",
              "Scroll down to the element Flight Summary Continue into view",
              "Click on Flight Summary Continue",
              "Click on Meals Continue button",
              "Click on Wallet Continue button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Search flights with multiple filters applied",
            "id": 4,
            "steps": [
              "Login step group (Reusable Steps)",
              "Click on Non Stop",
              "Click on Departure From",
              "Click on More Filters",
              "Click on Apply",
              "Click on Sort by button",
              "Click on Price",
              "Click on Book Flight button",
              "Scroll down to the element Review & Continue button into view",
              "Click on Review & Continue button",
              "Enter !|PhoneNumber - cellPhone()| in the Contact Mobile Number field",
              "Enter !|Internet - emailAddress()| in the Contact Email field",
              "Scroll down to the element Flight Summary Continue into view",
              "Click on Flight Summary Continue",
              "Click on Meals Continue button",
              "Click on Wallet Continue button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify signup with valid data (using functions to generate test data)",
            "id": 5,
            "steps": [
              "Navigate to http://travel.testsigma.com/signup",
              "Enter !|Name - name()| in the Full Name field",
              "Enter !|Internet - emailAddress()| in the Email field",
              "Enter !|PhoneNumber - phoneNumber()| in the Phone No field",
              "Enter !|Address - streetName()| in the Your Address field",
              "Click on Age Group",
              "Scroll to the element Signup Password into view",
              "Enter !|Internet - password()| in the Signup Password field",
              "Store the value displayed in the text box Signup Password field into a variable password",
              "Enter $|password| in the ReEnterPassword field",
              "Click on Signup Button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          }
        ],

        "mobile application": [
          {
            "title": "Verify Successful Login",
            "id": 1,
            "steps": [
              "Navigate to http://travel.testsigma.com",
              "Tap on Login Icon",
              "Tap on Login SImply Travel",
              "Enter admin in the Username field",
              "Enter 12345 in the Password field",
              "Tap on Login button",
              "Verify that the current page displays text SEARCH FLIGHTS"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Search flights by cities (Data Driven)",
            "id": 2,
            "steps": [
              "Go to http://travel.testsigma.com/login",
              "Enter admin in the Username field",
              "Enter 12345 in the Password field",
              "Tap on Login button",
              "Wait for 30 seconds",
              "Tap on From Location",
              "Wait for 2 seconds",
              "Tap on From City with text @|city|",
              "Tap on To Location",
              "Tap on To Location Dropdown List Item",
              "Tap on Depart Datepicker",
              "Wait until the element Today in Datepicker is visible",
              "Tap on Today in Datepicker",
              "Tap on Ok button",
              "Tap on Search Flights button",
              "Verify that the current page displays text BO | Bo-747"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify flight booking (Using reusable Step Group)",
            "id": 3,
            "steps": [
              "Login step group (Reusable Steps)",
              "Tap on From Location",
              "Wait for 2 seconds",
              "Tap on From City with text @|city|",
              "Tap on To Location",
              "Tap on To Location Dropdown List Item",
              "Tap on Depart Datepicker",
              "Wait until the element Today in Datepicker is visible",
              "Tap on Today in Datepicker",
              "Tap on Ok button",
              "Tap on Search Flights button",
              "Verify that the current page displays text BO | Bo-747",
              "Tap on Book Flight button",
              "Scroll down to the element Review & Continue button into view",
              "Tap on Review & Continue button",
              "Enter !|PhoneNumber - cellPhone()| in the Contact Mobile Number field",
              "Enter !|Internet - emailAddress()| in the Contact Email field",
              "Scroll down to the element Flight Summary Continue into view",
              "Tap on Flight Summary Continue",
              "Tap on Meals Continue button",
              "Tap on Wallet Continue button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Search flights with multiple filters applied",
            "id": 4,
            "steps": [
              "Login step group (Reusable Steps)",
              "Tap on Non Stop",
              "Tap on Departure From",
              "Tap on More Filters",
              "Tap on Apply",
              "Tap on Sort by button",
              "Tap on Price",
              "Tap on Book Flight button",
              "Scroll down to the element Review & Continue button into view",
              "Tap on Review & Continue button",
              "Enter !|PhoneNumber - cellPhone()| in the Contact Mobile Number field",
              "Enter !|Internet - emailAddress()| in the Contact Email field",
              "Scroll down to the element Flight Summary Continue into view",
              "Tap on Flight Summary Continue",
              "Tap on Meals Continue button",
              "Tap on Wallet Continue button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify signup with valid data (using functions to generate test data)",
            "id": 5,
            "steps": [
              "Navigate to http://travel.testsigma.com/signup",
              "Enter !|Name - name()| in the Full Name field",
              "Enter !|Internet - emailAddress()| in the Email field",
              "Enter !|PhoneNumber - phoneNumber()| in the Phone No field",
              "Enter !|Address - streetName()| in the Your Address field",
              "Tap on Age Group",
              "Scroll to the element Signup Password into view",
              "Enter !|Internet - password()| in the Signup Password field",
              "Store the value displayed in the Signup Password field into a variable password",
              "Enter $|password| in the ReEnterPassword field",
              "Tap on Signup Button"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          }
        ],
        "android": [
          {
            "title": "Login and verify the subscribed plan(Free)",
            "id": 1,
            "steps": [
              "Login step group (Reusable Steps)",
              "IF Tap on Reject save credentials",
              "Verify that the current page displays text sridhar473",
              "Verify that the current page displays text shirts469298679.wordpress.com",
              "Tap on Continue",
              "Verify that the element Plan displays text Free",
              "Tap on Plan",
              "Tap on Free Wordpress",
              "Tap on Close Free Wordpress Details",
              "Tap on Back Arrow",
              "Logout step group (Reusable Steps)"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify create a new post",
            "id": 2,
            "steps": [
              "Login step group (Reusable Steps)",
              "Tap on Continue",
              "Tap on New post icon",
              "Enter NewBlogTitleHere in the New title input field",
              "Store text from the element New title input into variable postname",
              "Tap on Publish",
              "Tap on Confirm Publish",
              "Tap on Blog Posts",
              "Verify that the current page displays text $|postname|",
              "Tap on Back Arrow",
              "Logout step group (Reusable Steps)"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify edit profile functionality",
            "id": 3,
            "steps": [
              "Login step group (Reusable Steps)",
              "Tap on Continue",
              "Tap on Profile image",
              "Tap on My profile",
              "Tap on Firstname to enable",
              "Verify that the current page displays text sridhar",
              "Tap on Ok",
              "Tap on Back Arrow",
              "Logout step group (Reusable Steps)"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Login to multiple accounts (Data Driven)",
            "id": 4,
            "steps": [
              "Launch App",
              "Tap on Login button",
              "Tap on Enter Email",
              "IF Tap on None of the above",
              "Enter @|Username| in the Enter Email field",
              "Tap on Next button",
              "Tap on Enter Password",
              "Enter @|Password| in the Password field",
              "Tap on Next button",
              "Verify that the current page displays text sridhar473",
              "Logout step group (Reusable Steps)"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          }
        ],
        "iso": [
          {
            "title": "Verify app launch and Home page",
            "id": 1,
            "steps": [
              "Launch App",
              "Verify that the screen orientation is Portrait",
              "Verify the App org.wordpressBS is installed",
              "Wait until the text Log In is present on the current page",
              "Wait until the element login button is visible",
              "Verify that the current page displays text Create a WordPress site",
              "Verify that the element login button is displayed"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify login page",
            "id": 2,
            "steps": [
              "Login step group (Reusable Steps)",
              "Verify that the element next is enabled",
              "Tap on next",
              "IF Verify that the element send link is displayed",
              "Tap on enter your password",
              "Enter !|Internet - password()| in the Wordpress Password field",
              "Tap on next login",
              "ELSE",
              "Verify that the current page displays text This email address is not registered on WordPress.com"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify login page (Data Driven)",
            "id": 3,
            "steps": [
              "Launch App",
              "Verify that the element login is displayed",
              "Tap on login",
              "IF Verify that the element email is displayed",
              "Enter @|Username| in the email field",
              "Verify that the element next is enabled",
              "Tap on next",
              "Verify that the element send link is displayed",
              "Verify that the element enter your password is displayed",
              "Tap on enter your password",
              "Enter @|Password| in the Wordpress Password field",
              "Tap on next login"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          },
          {
            "title": "Verify signup",
            "id": 4,
            "steps": [
              "Launch App",
              "IF Verify that the element create button has tag name XCUIElementTypeButton",
              "Tap on create button",
              "Verify that the element username is displayed",
              "Enter !|EmailFunctions - randomEmail(length,domain)| in the signup email field",
              "Enter !|Name - name()| in the username field",
              "Enter !|Internet - password()| in the password field",
              "Tap on create account",
              "Tap on OK button",
              "ELSE",
              "Verify the App org.wordpressBS is installed"],
            "stepDetails": [
              {"id":"1044", "testData":"http://travel.testsigma.com/"},
              {"id":"109", "element": "Login Icon"},
              {"id":"109", "element": "Login SImply Travel"},
              {"id":"971", "testData":"admin", "element": "Username"},
              {"id":"971", "testData":"12345", "element": "Password"},
              {"id":"109", "element": "Login button"},
              {"id":"35", "testData":"SEARCH FLIGHTS"},
            ]
          }
        ]
      }
    ]
    if (this.version && this.version.workspace)
      this.setSampleUseCase();
  }

  setSampleUseCase() {
    if (this.version.workspace.isWeb) {
      this.sampleUseCaseByApp = this.sampleUseCases[0]['web application'];
    } else if (this.version.workspace.isMobileWeb) {
      this.sampleUseCaseByApp = this.sampleUseCases[0]['mobile application'];
    } else if (this.version.workspace.isAndroidNative) {
      this.sampleUseCaseByApp = this.sampleUseCases[0]['android'];
    } else if (this.version.workspace.isIosNative) {
      this.sampleUseCaseByApp = this.sampleUseCases[0]['iso'];
    }
  }

  toggleUseCase(useCase: any) {
    if (this.selectedUseCase && useCase.id == this.selectedUseCase.id) {
      this.selectedUseCase = [];
      return false;
    }
    this.selectedUseCase = useCase;
  }
}
