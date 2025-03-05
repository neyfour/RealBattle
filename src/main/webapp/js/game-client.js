document.addEventListener('DOMContentLoaded', function() {
    // Get session ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const sessionId = urlParams.get('sessionId');
    
    if (!sessionId) {
        alert('No session ID provided. Returning to menu.');
        window.location.href = '/';
        return;
    }
    
    // DOM elements
    const gameCanvas = document.getElementById('game-canvas');
    const playerHealth = document.getElementById('player-health');
    const playerXp = document.getElementById('player-xp');
    const attackBtn = document.getElementById('attack-btn');
    const specialBtn = document.getElementById('special-btn');
    const gameOver = document.getElementById('game-over');
    const gameResult = document.getElementById('game-result');
    const gameStats = document.getElementById('game-stats');
    const returnToMenuBtn = document.getElementById('return-to-menu');
    
    // Game state
    let webSocket = null;
    let applet = null;
    
    // Connect to WebSocket
    function connectToGameSession() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/game/${sessionId}`;
        
        webSocket = new WebSocket(wsUrl);
        
        webSocket.onopen = function() {
            console.log('Connected to game session');
        };
        
        webSocket.onmessage = function(event) {
            const data = JSON.parse(event.data);
            
            if (data.type === 'error') {
                alert('Error: ' + data.message);
                return;
            }
            
            // Update game state
            updateGameState(data);
            
            // If game is completed, show game over screen
            if (data.state === 'COMPLETED') {
                showGameOver(data);
            }
        };
        
        webSocket.onclose = function() {
            console.log('Disconnected from game session');
        };
        
        webSocket.onerror = function(error) {
            console.error('WebSocket error:', error);
            alert('Error connecting to game session. Returning to menu.');
            window.location.href = '/';
        };
    }
    
    // Initialize jMonkeyEngine applet
    function initializeApplet() {
        // The applet is loaded from the Java EE server
        // This is a simplified representation
        applet = document.createElement('applet');
        applet.code = 'com.game.client.GameClientApplet.class';
        applet.archive = 'game-client.jar';
        applet.width = gameCanvas.clientWidth;
        applet.height = gameCanvas.clientHeight;
        
        // Add parameters
        const sessionParam = document.createElement('param');
        sessionParam.name = 'sessionId';
        sessionParam.value = sessionId;
        applet.appendChild(sessionParam);
        
        // Replace canvas with applet
        gameCanvas.parentNode.replaceChild(applet, gameCanvas);
    }
    
    // Update game state based on WebSocket data
    function updateGameState(data) {
        // Update player stats
        if (data.player) {
            playerHealth.textContent = data.player.health;
            playerXp.textContent = data.player.experience;
        }
        
        // Update applet with game state
        if (applet && applet.updateGameState) {
            applet.updateGameState(JSON.stringify(data));
        }
    }
    
    // Show game over screen
    function showGameOver(data) {
        gameOver.style.display = 'block';
        
        if (data.victory) {
            gameResult.textContent = 'Victory!';
            gameStats.textContent = 'You defeated all enemies!';
        } else {
            gameResult.textContent = 'Defeat!';
            gameStats.textContent = 'All players have been defeated.';
        }
    }
    
    // Event listeners
    attackBtn.addEventListener('click', function() {
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify({
                action: 'attack',
                targetId: getClosestEnemyId() // Function to determine closest enemy
            }));
        }
    });
    
    specialBtn.addEventListener('click', function() {
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify({
                action: 'special',
                targetId: getClosestEnemyId()
            }));
        }
    });
    
    returnToMenuBtn.addEventListener('click', function() {
        window.location.href = '/';
    });
    
    // Helper function to get closest enemy ID
    function getClosestEnemyId() {
        // In a real implementation, this would use the 3D positions
        // to determine the closest enemy
        // For simplicity, we'll return a placeholder
        return 'enemy-0';
    }
    
    // Initialize the game
    connectToGameSession();
    initializeApplet();
});