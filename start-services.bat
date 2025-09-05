@echo off
echo Starting PFE Microservices...

echo Starting Config Server...
start "Config Server" cmd /k "cd services\config-server && mvn spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Discovery Service...
start "Discovery Service" cmd /k "cd services\discovery && mvn spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Gateway...
start "Gateway" cmd /k "cd services\gateway && mvn spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting User Service...
start "User Service" cmd /k "cd services\user && mvn spring-boot:run"

timeout /t 5 /nobreak > nul

echo Starting Contract Service...
start "Contract Service" cmd /k "cd services\contract && mvn spring-boot:run"

timeout /t 5 /nobreak > nul

echo Starting Notifications Service...
start "Notifications Service" cmd /k "cd services\notifications && mvn spring-boot:run"

timeout /t 5 /nobreak > nul

echo Starting Payment Service...
start "Payment Service" cmd /k "cd services\payment && mvn spring-boot:run"

echo All services started! Check the individual windows for status.
echo.
echo Services should be available at:
echo - Gateway: http://localhost:8222
echo - Config Server: http://localhost:8888
echo - Discovery: http://localhost:8761
echo - User Service: http://localhost:8087
echo - Contract Service: http://localhost:8080
echo - Notifications Service: http://localhost:8081
echo - Payment Service: http://localhost:8089
echo.
pause
