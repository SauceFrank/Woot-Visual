# Woot.com Visual Demo

4 Concurrent Sessions

Runs visual test on Woot.com Site.

Environment Variables Required:

SAUCE_USERNAME   
SAUCE_ACCESS_KEY   
BUILD_TAG   

If Sauce Connect Tunnel is needed, set following environment variable   
SAUCE_TUNNEL    

If Sauce Connect Tunnel is not needed, comment out the following line in Capabilities   
sauceOptions.setCapability("tunnelIdentifier", sauceTunnel);    

Windows 10 Chrome 98.0, and Windows 10 Firefox 2 versions back

Runs Visual test against Woot.com. Run to set baseline by accepting all changes.   
Then run later or the next day to see the changes.

mvn dependency:resolve    
mvn test-compile

Run all tests with: mvn test
