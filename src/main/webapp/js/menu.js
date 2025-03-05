document.addEventListener('DOMContentLoaded', function() {
    // DOM elements
    const startGameBtn = document.getElementById('start-game');
    const joinGameBtn = document.getElementById('join-game');
    const cancelSessionBtn = document.getElementById('cancel-session');
    const playerForm = document.getElementById('player-form');
    const joinForm = document.getElementById('join-form');
    const sessionInfo = document.getElementById('session-info');
    const createPlayerBtn = document.getElementById('create-player');
    const cancelPlayerBtn = document.getElementById('cancel-player');
    const joinSessionBtn = document.getElementById('join-session');
    const cancelJoinBtn = document.getElementById('cancel-join');
    const startSessionBtn = document.getElementById('start-session');
    const leaveSessionBtn = document.getElementById('leave-session');
    const currentSessionId = document.getElementById('current-session-id');
    const playerCount = document.getElementById('player-count');
    
    // Session state
    let activeSessionId = null;
    let playerName = null;
    let playerType = null;
    let webSocket = null;
    
    // Event listeners
    startGameBtn.addEventListener('click', function() {
        // Create a new game session
        fetch('CreateSessionServlet', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            activeSessionId = data.sessionId;
            currentSessionId.textContent = activeSessionId;
            
            // Show player creation form
            playerForm.style.display = 'block';
        })
        .catch(error => {
            console.error('Error creating session:', error);
            alert('Failed to create game session. Please try again.');
        });
    });
    
    joinGameBtn.addEventListener('click', function() {
        // Show join session form
        joinForm.style.display = 'block';
    });
    
    cancelSessionBtn.addEventListener('click', function() {
        if (activeSessionId && webSocket) {
            webSocket.close();
            activeSessionId = null;
            playerName = null;
            playerType = null;
            
            // Reset UI
            sessionInfo.style.display = 'none';
        }
    });
    
    // Character selection
    const characterOptions = document.querySelectorAll('.character-option');
    characterOptions.forEach(option => {
        option.addEventListener('click', function() {
            // Remove selected class from all options
            characterOptions.forEach(opt => opt.classList.remove('selected'));
            
            // Add selected class to clicked option
            this.classList.add('selected');
            
            // Store selected player type
            playerType = this.getAttribute('data-type');
        });
    });
    
    createPlayerBtn.addEventListener('click', function() {
        playerName = document.getElementById('player-name').value;
        
        if (!playerName || !playerType) {
            alert('Please enter a name and select a character type.');
            return;
        }
        
        // Hide player form and show session info
        playerForm.style.display = 'none';
        sessionInfo.style.display = 'block';
        
        // Connect to WebSocket
        connectToGameSession(activeSessionId);
    });
    
    cancelPlayerBtn.addEventListener('click', function() {
        playerForm.style.display = 'none';
        
        // Cancel the session if it was just created
        if (activeSessionId) {
            fetch(`/api/sessions/cancel?sessionId=${activeSessionId}`, {
                method: 'POST'
            });
            activeSessionId = null;
        }
    });
    
    joinSessionBtn.addEventListener('click', function() {
        const sessionId = document.getElementById('session-id').value;
        
        if (!sessionId) {
            alert('Please enter a session code.');
            return;
        }
        
        // Check if session exists
        fetch(`/api/sessions/join?sessionId=${sessionId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Session not found');
            }
            return response.json();
        })
        .then(data => {
            activeSessionId = data.sessionId;
            currentSessionId.textContent = activeSessionId;
            playerCount.textContent = data.playerCount;
            
            // Hide join form and show player creation form
            joinForm.style.display = 'none';
            playerForm.style.display = 'block';
        })
        .catch(error => {
            console.error('Error joining session:', error);
            alert('Failed to join game session. Please check the session code and try again.');
        });
    });
    
    cancelJoinBtn.addEventListener('click', function() {
        joinForm.style.display = 'none';
    });
    
    startSessionBtn.addEventListener('click', function() {
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            // Send start game message
            webSocket.send(JSON.stringify({
                action: 'start'
            }));
        }
    });
    
    leaveSessionBtn.addEventListener('click', function() {
        if (webSocket) {
            webSocket.close();
        }
        
        activeSessionId = null;
        playerName = null;
        playerType = null;
        
        // Reset UI
        sessionInfo.style.display = 'none';
    });
    
    function connectToGameSession(sessionId) {
        // Close existing connection if any
        if (webSocket) {
            webSocket.close();
        }
        
        // Create new WebSocket connection
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/game/${sessionId}`;
        
        webSocket = new WebSocket(wsUrl);
        
        webSocket.onopen = function() {
            console.log('Connected to game session');
            
            // Join the game session
            webSocket.send(JSON.stringify({
                action: 'join',
                playerName: playerName,
                playerType: playerType
            }));
        };
        
        webSocket.onmessage = function(event) {
            const data = JSON.parse(event.data);
            
            if (data.type === 'error') {
                alert('Error: ' + data.message);
                return;
            }
            
            // Update player count
            if (data.playerCount) {
                playerCount.textContent = data.playerCount;
            }
            
            // If game is starting, redirect to game page
            if (data.state === 'ACTIVE') {
                window.location.href = `/game.html?sessionId=${sessionId}`;
            }
        };
        
        webSocket.onclose = function() {
            console.log('Disconnected from game session');
        };
        
        webSocket.onerror = function(error) {
            console.error('WebSocket error:', error);
            alert('Error connecting to game session. Please try again.');
        };
    }
});