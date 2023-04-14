# Makudu Demo

18 Concurrent Sessions   
4 Real Device Sessions

Runs test on Makudu Site.

Cross Browser, Android, and iOS Real Devices

mvn dependency:resolve    
mvn test-compile

Run all tests with: mvn test

### Run via Saucectl in [Hosted Orchestration](https://docs.saucelabs.com/hosted-orchestration/)

```shell
export SAUCE_REGION="us-west-1"
saucectl run
```