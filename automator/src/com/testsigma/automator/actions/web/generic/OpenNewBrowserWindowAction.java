package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.apache.http.HttpStatus;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.*;

@Log4j2
public class OpenNewBrowserWindowAction extends ElementAction {
    private static final String SUCCESS_MESSAGE = "Successfully opened given URL.";

    @Override
    protected void execute() throws Exception {
        String navigationUrl = getTestData().trim();
        log.info("Opening a new browser window and navigating to - " + navigationUrl);
        getDriver().switchTo().newWindow(WindowType.WINDOW);
        getDriver().navigate().to(navigationUrl);
        setSuccessMessage(SUCCESS_MESSAGE);
    }

    @Override
    protected void handleException(Exception e) {
        super.handleException(e);
        if (e instanceof UnreachableBrowserException || e instanceof NoSuchSessionException) {
            //These two exception types are already handled in super class.
        } else if (e instanceof TimeoutException) {
            setErrorMessage(String.format("The Web Page with URL <b>\"%s\"</b> failed to load within the configured page load timeout duration. " +
                    "Please increase the 'page load timeout' in Test Step Settings or Test Plan Configuration if this delay is expected.", getTestData()));
        } else if (e instanceof WebDriverException) {
            try {
                URL url = new URL(getTestData());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(30 * 1000);
                httpURLConnection.connect();
                int status_code = httpURLConnection.getResponseCode();

                switch (status_code) {
                    case HttpStatus.SC_BAD_REQUEST:
                        setErrorCode(ErrorCodes.HTTP_BAD_REQUEST);
                        setErrorMessage(String.format("Cannot navigate to invalid URL <b>\"%s\"</b> - " +
                                "Please check the URL for invalid characters and also make sure it contains the correct prefix http:// or https://", getTestData()));
                    case HttpStatus.SC_UNAUTHORIZED:
                        setErrorCode(ErrorCodes.HTTP_UNAUTHORIZED);
                        setErrorMessage(String.format("The requested Web Page <b>\"%s\"</b> requires extra Authentication. " +
                                "Please pass the URL in the format 'https://&lt;username&gt;:&lt;password&gt;@&lt;url&gt;' by replacing the values for 'username', 'password' and 'url' " +
                                "if the page accepts Basic Authentication. <br><br>For more information, " +
                                "please refer - <a class=\"text-link\" href=\"https://testsigma.freshdesk.com/support/solutions/articles/32000024278-how-to-perform-basic-authentication-for-web-pages-in-testsigma-\"" +
                                " target=\"_blank\">How to perform Basic Authentication for Web pages in Testsigma?</a>", getTestData()));
                    case HttpStatus.SC_FORBIDDEN:
                        setErrorCode(ErrorCodes.HTTP_FORBIDDEN);
                        setErrorMessage(String.format("The requested Web Page <b>\"%s\"</b> requires extra Authentication. " +
                                "Please pass the URL in the format 'https://&lt;username&gt;:&lt;password&gt;@&lt;url&gt;' by replacing the values " +
                                "for 'username', 'password' and 'url' if the page accepts Basic Authentication. <br><br>For more information, " +
                                "please refer - <a class=\"text-link\" href=\"https://testsigma.freshdesk.com/support/solutions/articles/32000024278-how-to-perform-basic-authentication-for-web-pages-in-testsigma-\" " +
                                "target=\"_blank\">How to perform Basic Authentication for Web pages in Testsigma?</a>", getTestData()));
                    case HttpStatus.SC_NOT_FOUND:
                        setErrorCode(ErrorCodes.HTTP_NOT_FOUND);
                        setErrorMessage(String.format("The requested Web Page/Resource <b>\"%s\"</b> cannot be found on the Web Server. " +
                                "Please try the URL once in your Web Browser manually before trying again.", getTestData()));
                    case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
                        setErrorCode(ErrorCodes.HTTP_PROXY_AUTHENTICATION_REQUIRED);
                        setErrorMessage(String.format("The requested Web Page <b>\"%s\"</b> requires Proxy Authentication. " +
                                "Please use 'Hybrid Execution' for running the Test on the corresponding URL.", getTestData()));
                    case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                        setErrorCode(ErrorCodes.HTTP_INTERNAL_SERVER_ERROR);
                        setErrorMessage("The corresponding web server returned an unexpected error. Please check the URL once before trying again. " +
                                "If this page belongs to you, please contact the Admininstrator of this Domain/Web Application");
                    default:
                        if (Integer.toString(status_code).startsWith("4")) {
                            setErrorCode(ErrorCodes.HTTP_GENERIC_CLIENT_SIDE_ERROR);
                            setErrorMessage(String.format("HTTP Error %s - The page returned a Client Side HTTP Error while trying to load the URL %s",
                                    status_code, getTestData()));
                        } else if (Integer.toString(status_code).startsWith("5")) {
                            setErrorCode(ErrorCodes.HTTP_GENERIC_SERVER_SIDE_ERROR);
                            setErrorMessage(String.format("HTTP Error %s - The page returned a Server Side HTTP Error while trying to load the URL %s. " +
                                    "Please contact the Server Administrator for more help.", status_code, getTestData()));
                        }
                }
            } catch (MalformedURLException malformurlex) {
                setErrorCode(ErrorCodes.HTTP_MALFORMED_YRL_EXCEPTION);
                setErrorMessage(String.format("Cannot navigate to invalid URL <b>\"%s\"</b> - " +
                        "Please check the URL for invalid characters and also make sure it contains the correct prefix http:// or https://", getTestData()));
            } catch (SocketTimeoutException socktimeoutex) {
                String errorMsg = "The requested Web Page/Resource <b>\"%s\"</b> took too long to respond and is not reachable currently. " +
                        "If you are trying to test a local Application and this is a local IP/Address, please use 'Hybrid Execution' for running the Test" +
                        ".<br><br>For more information, please refer - <a class=\"text-link\" " +
                        "href=\"https://testsigma.com/docs/faqs/web-apps/why-cloud-devices-cannot-access-local-apps/\"" +
                        "target=\"_blank\">Why Cloud Test Environments can't access Locally hosted Applications?</a>.";
                setErrorCode(ErrorCodes.HTTP_SOCKET_EXCEPTION);
                setErrorMessage(String.format(errorMsg, getTestData()));
            } catch (UnknownHostException ex) {
                setErrorCode(ErrorCodes.HTTP_UNKNOWN_HOST_EXCEPTION);
                String errorMsg = "The requested Web Page/Resource <b>\"%s\"</b> not respond and is not reachable currently. " +
                        "If you are trying to test a local Application and this is a local IP/Address, please use 'Hybrid Execution' for running the Test" +
                        ".<br><br>For more information, please refer - <a class=\"text-link\" " +
                        "href=\"https://testsigma.com/docs/faqs/web-apps/why-cloud-devices-cannot-access-local-apps/\"" +
                        "target=\"_blank\">Why Cloud Test Environments can't access Locally hosted Applications?</a>.";
                setErrorMessage(String.format(errorMsg, getTestData(), e.getMessage()));
            } catch (IOException ioe) {
                setErrorCode(ErrorCodes.HTTP_IO_EXCEPTION);
                setErrorMessage(String.format("Cannot navigate to URL <b>\"%s\"</b> - due to below error<br> %s", getTestData(), e.getMessage()));
            } catch (Throwable ex) {
                setErrorMessage(String.format("Unable to load given URL <b>\"%s\"</b>,please provide a valid URL.", getTestData()));
            }
        } else if (e instanceof MalformedURLException) {
            setErrorCode(ErrorCodes.HTTP_MALFORMED_YRL_EXCEPTION);
            setErrorMessage(String.format("Cannot navigate to invalid URL <b>\"%s\"</b> - " +
                    "Please check the URL for invalid characters and also make sure it contains the correct prefix http:// or https://", getTestData()));
        } else if (e instanceof IOException) {
            setErrorCode(ErrorCodes.HTTP_IO_EXCEPTION);
            setErrorMessage(String.format("Cannot navigate to URL <b>\"%s\"</b> - due to below error<br> %s", getTestData(), e.getMessage()));
        } else {
            setErrorMessage(String.format("Unable to load given URL <b>\"%s\"</b>,please verify the URL once.", getTestData()));
        }
    }
}
