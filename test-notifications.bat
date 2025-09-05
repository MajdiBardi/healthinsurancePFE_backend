@echo off
echo Testing Notifications Service...
echo.

echo 1. Testing direct access to notifications service (port 8081)...
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8081/actuator/health
if %errorlevel% == 0 (
    echo ✓ Notifications Service is running on port 8081
) else (
    echo ✗ Notifications Service is NOT running on port 8081
)

echo.
echo 2. Testing gateway access to notifications (port 8222)...
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8222/api/notifications
if %errorlevel% == 0 (
    echo ✓ Gateway can reach notifications service
) else (
    echo ✗ Gateway cannot reach notifications service
)

echo.
echo 3. Testing with a simple GET request...
curl -s -w "HTTP Status: %%{http_code}\n" http://localhost:8222/api/notifications/user/test-user

echo.
echo 4. Testing contract service (should work)...
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8222/api/contracts

echo.
echo Test complete!
echo.
echo If you see 403 errors, the issue is likely:
echo - JWT token permissions
echo - Service not registered with Eureka
echo - Security configuration
echo.
pause
