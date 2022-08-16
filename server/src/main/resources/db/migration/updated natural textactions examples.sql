UPDATE natural_text_action_examples
SET example='In this example, execution will wait until specified text "\Register\" is present on current Page', workspace='https://travel.testsigma.com/login', `data`='{"test data": "Register"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=1;
UPDATE natural_text_action_examples
SET  example='In this example, execution will wait until specified text "\HOME123\" is absent on current Page', workspace='https://demoqa.com/', `data`='{"test data": "HOME123"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=2;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', `data`='{"element": {"type": "id ", "value": "first_name "}, "test data": " test321"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=6;
UPDATE natural_text_action_examples
SET  example='In this example, execution will wait until the specified attribute of Element with given Locator "Username" is changed', workspace='https://travel.testsigma.com/login', `data`='{"type": "id ", "value": "first_name "}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=13;
UPDATE natural_text_action_examples
SET workspace='https://demoqa.com/alerts', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=18;
UPDATE natural_text_action_examples
SET workspace='https://app.testsigma.com', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=20;
UPDATE natural_text_action_examples
SET  example='In this example, execution will wait until all Elements with specified Class Name "text-center" are displayed', workspace='https://travel.testsigma.com/signup', `data`='{"test data": "text-center"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=21;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=22;
UPDATE natural_text_action_examples
SET   example='In this example, we can verify that the current Page displays an Element with given Locator "submit"', workspace='https://travel.testsigma.com/login', `data`='{"submit": {"type": "name", "value": "submit"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=29;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/signup', `data`='{"Interests": {"type": "id", "value": "Job"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=31;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"register": {"type": "xpath", "value": "//input[@id=''btnid'']"}, "attribute": "type", "test data": "register"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=33;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the List with given Element "Hobbies" has option with specified text "Cricket" selected', workspace='https://travel.testsigma.com/signup', `data`='{"testdata": "Cricket", "Option_text": {"type": "id", "value": "Hobbies"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=34;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the Element with given Locator "UserName" has a non-empty value', workspace='https://travel.testsigma.com/login', `data`='{"Login": {"type": "id", "value": "first_name"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=36;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the Element with given Locator "submit" displays specified value "register" for specified attribute "type"', workspace='https://travel.testsigma.com/signup', `data`='{"register": {"type": "xpath", "value": "//input[@id=''btnid'']"}, "attribute": "type", "test data": "register"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=40;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the Element with given Locator "register" displays specified value "register"', workspace='https://travel.testsigma.com/signup', `data`='{"register": {"type": "submit"}, "test data": "REGISTER"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=41;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/signup',  created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=48;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=49;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/signup',created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=50;
UPDATE natural_text_action_examples
SET example='In this example, we Store the current Page URL into specified variable "s_url"', workspace='https://travel.testsigma.com/signup', `data`='{"test data": "s_url"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=51;
UPDATE natural_text_action_examples
SET example='In this example, we Store the text from selected option in a given List "Select Shapes" into a specified variable "s_stext"', workspace='https://examples.testsigma.com/dropdownpage', `data`='{"Sel_shape": {"type": "shapes", "value": "circle"}, "test data": "s_stag"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=52;
UPDATE natural_text_action_examples
SET example='In this example, we Store the total count of options in a given Element "sel_shape" into a specified variable "s_count"', workspace='https://examples.testsigma.com/dropdownpage', `data`='{"Sel_shape": {"type": "shapes", "value": "circle"}, "test data": "s_stext"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=53;
UPDATE natural_text_action_examples
SET example='In this example, we Store the total count of all Elements located by given Element "Sel_shape" into a specified variable "s_ecount"', workspace='https://examples.testsigma.com/dropdownpage', `data`='{"Sel_shape": {"type": "shapes", "value": "circle"}, "test data": "s_ecount"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=54;
UPDATE natural_text_action_examples
SET example='In this example, we Store the text from Element with given Locator "username" into a specified variable "s_text".', workspace='https://travel.testsigma.com/login', `data`='{"USERNAME": {"type": "id", "value": "first_name"}, "test data": "s_text"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=55;
UPDATE testsigma_opensource.natural_text_action_examples
SET workspace='https://examples.testsigma.com/dropdownpage',created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=56;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Gender"}, "test data": "Male"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=64;
UPDATE natural_text_action_examples
SET example='In this example, we navigate to a WebPage with URL "http://app.testsigma.com"', `data`='{"test data": "app.testsigma.com"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=80;
UPDATE natural_text_action_examples
SET  workspace='http://app.testsigma.com',`data`='{"test data": "1"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=85;
UPDATE natural_text_action_examples
SET example='In this example, we close the window that has TitleLogin - AI Powered Test Automation Platform | Testsigma ', workspace='https://app.testsigma.com',`data`='{"text data": "Login - AI Powered Test Automation Platform | Testsigma"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=86;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=87;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=88;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', `data`='{"USERNAME": {"type": "id", "value": "first_name"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=94;
UPDATE natural_text_action_examples
SET  workspace='https://app.testsigma.com', `data`='{"USERNAME": {"type": "id", "value": "loginUsername"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=95;
UPDATE natural_text_action_examples
SET workspace='https://app.testsigma.com',  created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=96;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/login', `data`='{"USERNAME": {"type": "id", "value": "first_name"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=100;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"test data": "https://travel.testsigma.com/signup"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=172;
UPDATE natural_text_action_examples
SET example='https://travel.testsigma.com/signup', workspace='{"test data": "https://travel.testsigma.com/signup"}', `data`='', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=225;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', `data`='{"username": {"type": "id", "value": "first_name"}, "test data": "admin"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=266;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/login', `data`='{"username": {"type": "id", "value": "first_name"}, "test data": "testsigma"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=308;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', `data`='{"fields": {"type": "xpath", "value": "//a[@class=''float-right '']"}, "test data": "1"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=309;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/signup', `data`='{"Male": {"type": "xpath", "value": "//input[@value=''Male'']"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=317;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"partialLink": {"type": "xpath", "value": "//a[@class=''dropdown-item'']"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=318;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/login', `data`='{"first": {"type": "xpath", "value": "//input[@id=''first_name'']"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=319;
UPDATE natural_text_action_examples
SET  workspace='https://www.dianova.org/privacy-policy/cookies/?gclid=Cj0KCQjwrs2XBhDjARIsAHVymmRsZ9z80yyQUG7FrAfIwYHY1-4E8bxR56Gw2uXkGfepSwJkqa3AvuwaAsmwEALw_wcB', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=322;
UPDATE natural_text_action_examples
SET example='In this example, we Select multiple options by value "Cricket,Hockey,Movies" from the list with given Element "Sel_Multi"', workspace='https://travel.testsigma.com/signup', `data`='{"Sel_Multi": {"type": "id", "value": "multi-select"}, "test data": "Cricket,Movies, Hockey"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=323;
UPDATE natural_text_action_examples
SET example='In this example, we Select multiple options by text "Cricket,Hockey,Movies" from the list with given Element "Sel_Multi"', workspace='https://travel.testsigma.com/signup', `data`='{"Sel_Multi": {"type": "id", "value": "multi-select"}, "test data": "Cricket,Movies,Hockey"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=324;
UPDATE natural_text_action_examples
SET example='In this example, we Select multiple options by index "1,2,3" from the list with given Element "Sel_Multi"', workspace='https://travel.testsigma.com/signup', `data`='{"Sel_Multi": {"type": "id", "value": "multi-select"}, "test data": "1,2,3"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=325;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Store_sel": {"type": "name", "value": "Gender"}, "test data": "Female"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=328;
UPDATE testsigma_opensource.natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Gender"}, "test data": "1"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=329;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Gender"}, "test data": "Female"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=330;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Male"}, "test data": "Male"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=331;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Gender"}, "test data": "Female"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=332;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"hob_group": {"type": "name", "value": "//ul[@class=''checkbox'']/li/label"}, "test data": "Cricket"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=333;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/login', `data`='{"type": "xpath", "test data": "up"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=334;
UPDATE natural_text_action_examples
SET example='In this example, we navigate to a WebPage with URL "http://app.testsigma.com"',workspace='http://app.testsigma.com', `data`='{"test data": "http://app.testsigma.com"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=339;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/login', `data`='{"Link": {"type": "Xpath", "value": "//a[@href=''/'']"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=340;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the Element with given Locator "script" has a non-empty innerText', workspace='https://travel.testsigma.com/login', `data`='{"script": {"type": "Xpath", "value": "//script[@type=''text/javascript'']"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=341;
UPDATE natural_text_action_examples
SET example='In this example, we can verify that the given Element "username" displays Contains value "Test"', workspace='https://travel.testsigma.com/login', `data`='{"username": {"type": "xpath", "value": "//input[@name=''name'']"}, "test data": "Test"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=342;
UPDATE natural_text_action_examples
SET example='In this example, we navigate to a WebPage with URL "http://app.testsigma.com"', workspace='http://app.testsigma.com', `data`='{"text data": "http://app.testsigma.com"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=384;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"register": {"type": "xpath", "value": "//input[@id=''btnid'']"}, "attribute": "type", "test data": "register"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=425;
UPDATE natural_text_action_examples
SET example='In this example, execution will wait until specified text "Register" is present on current Page', workspace='https://travel.testsigma.com/login', `data`='{"test data": "Register"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=456;
UPDATE natural_text_action_examples
SET example='', workspace='https://travel.testsigma.com/login', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=472;
UPDATE natural_text_action_examples
SET  workspace='https://www.dianova.org/privacy-policy/cookies/?gclid=Cj0KCQjwrs2XBhDjARIsAHVymmRsZ9z80yyQUG7FrAfIwYHY1-4E8bxR56Gw2uXkGfepSwJkqa3AvuwaAsmwEALw_wcB', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=478;
UPDATE natural_text_action_examples
SET  workspace='https://travel.testsigma.com/signup', `data`='{"Gen_sel": {"type": "name", "value": "Gender"}, "test data": "Male"}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=485;
UPDATE natural_text_action_examples
SET workspace='https://travel.testsigma.com/login', `data`='{"USERNAME": {"type": "id", "value": "first_name"}}', created_date=CURRENT_TIMESTAMP, updated_date=CURRENT_TIMESTAMP
WHERE id=504;
