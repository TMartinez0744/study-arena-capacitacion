import { useState } from 'react';
import axios from 'axios';

function App() {
  const [status, setStatus] = useState<string>('');

  const checkBackend = async () => {
    try {
      const response = await axios.get(`${import.meta.env.VITE_API_URL}/ping`);
      setStatus(JSON.stringify(response.data));
    } catch (error) {
      setStatus('Error: no responde el backend');
    }
  };

  return (
      <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
        <h1>Capacitación</h1>
        <button onClick={checkBackend}>Probar backend</button>
        <p>{status}</p>
      </div>
  );
}

export default App;