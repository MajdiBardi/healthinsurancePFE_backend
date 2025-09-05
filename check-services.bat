@echo off
echo Checking PFE Microservices Status...
echo.

echo Checking Config Server (8888)...
curl -s http://localhost:8888/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Config Server: RUNNING
) else (
    echo ✗ Config Server: NOT RUNNING
)

echo Checking Discovery Service (8761)...
curl -s http://localhost:8761/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Discovery Service: RUNNING
) else (
    echo ✗ Discovery Service: NOT RUNNING
)

echo Checking Gateway (8222)...
curl -s http://localhost:8222/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Gateway: RUNNING
) else (
    echo ✗ Gateway: NOT RUNNING
)

echo Checking User Service (8087)...
curl -s http://localhost:8087/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ User Service: RUNNING
) else (
    echo ✗ User Service: NOT RUNNING
)

echo Checking Contract Service (8080)...
curl -s http://localhost:8080/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Contract Service: RUNNING
) else (
    echo ✗ Contract Service: NOT RUNNING
)

echo Checking Notifications Service (8081)...
curl -s http://localhost:8081/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Notifications Service: RUNNING
) else (
    echo ✗ Notifications Service: NOT RUNNING
)

echo Checking Payment Service (8089)...
curl -s http://localhost:8089/actuator/health > nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Payment Service: RUNNING
) else (
    echo ✗ Payment Service: NOT RUNNING
)

echo.
echo Testing Gateway Routes...
echo Testing /api/contracts...
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8222/api/contracts

echo Testing /api/notifications...
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8222/api/notifications

echo.
echo Services check complete!
pause
