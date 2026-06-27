import React, { useState } from 'react';
import { Send } from 'lucide-react';

interface Message {
  id: number;
  text: string;
  sender: string;
  timestamp: string;
}

interface Request {
  id: number;
  reason: string;
  subject: string;
  date: string;
  messages: Message[];
}

const AssistancePage: React.FC = () => {
  const [showNewRequest, setShowNewRequest] = useState(false);
  const [reason, setReason] = useState('');
  const [subject, setSubject] = useState('');
  const [requests, setRequests] = useState<Request[]>([
    {
      id: 1,
      reason: 'Atraso na Entrega',
      subject: 'Boa tarde, estou com um atraso na entrega do Hospital devido a trânsito intenso na Av. da Noruega. Vou chegar cerca de 20 minutos mais tarde que o previsto. A entrega estava marcada para as 09:00 mas só conseguirei chegar às 09:20. Peço desculpa pelo inconveniente.',
      date: new Date().toLocaleDateString(),
      messages: [
        {
          id: 1,
          text: 'Boa tarde, estou com um atraso na entrega do Hospital devido a trânsito intenso na Av. da Noruega. Vou chegar cerca de 20 minutos mais tarde que o previsto. A entrega estava marcada para as 09:00 mas só conseguirei chegar às 09:20. Peço desculpa pelo inconveniente.',
          sender: 'driver',
          timestamp: '14:32'
        },
        {
          id: 2,
          text: 'Obrigado pela informação, João. Já contactei o Hospital para informar sobre o atraso. Não há problema, o trânsito está mesmo complicado hoje. Mantenha-nos informados se houver mais algum imprevisto.',
          sender: 'support',
          timestamp: '14:35'
        },
        {
          id: 3,
          text: 'Perfeito, obrigado! Já consegui passar o trânsito mais intenso. Devo chegar ao Hospital dentro de 10 minutos.',
          sender: 'driver',
          timestamp: '14:45'
        }
      ]
    }
  ]);
  const [selectedRequest, setSelectedRequest] = useState<Request | null>(null);
  const [newMessage, setNewMessage] = useState('');

  const handleSubmit = () => {
    if (reason && subject) {
      const newRequest = {
        id: requests.length + 1,
        reason,
        subject,
        date: new Date().toLocaleDateString(),
        messages: [{
          id: 1,
          text: subject,
          sender: 'user',
          timestamp: new Date().toLocaleTimeString()
        }]
      };
      setRequests([newRequest, ...requests]);
      setReason('');
      setSubject('');
      setShowNewRequest(false);
    }
  };

  const handleSendMessage = () => {
    if (newMessage.trim() && selectedRequest) {
      const message = {
        id: selectedRequest.messages.length + 1,
        text: newMessage,
        sender: 'user',
        timestamp: new Date().toLocaleTimeString()
      };
      
      const updatedRequest = {
        ...selectedRequest,
        messages: [...selectedRequest.messages, message]
      };

      setRequests(requests.map(req => 
        req.id === selectedRequest.id ? updatedRequest : req
      ));
      
      setSelectedRequest(updatedRequest);
      setNewMessage('');
    }
  };

  const renderChat = () => (
    <div className="flex-1 bg-white dark:bg-gray-800 rounded-md p-4 shadow-md flex flex-col">
      <div className="flex justify-between items-center mb-4 pb-3 border-b">
        <div>
          <h2 className="font-semibold text-lg">{selectedRequest?.reason}</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">{selectedRequest?.date}</p>
        </div>
        <button
          onClick={() => setSelectedRequest(null)}
          className="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:text-gray-200"
        >
          Voltar
        </button>
      </div>

      <div className="flex-1 overflow-y-auto mb-4 space-y-4">
        {selectedRequest?.messages.map(message => (
          <div
            key={message.id}
            className={`flex ${message.sender === 'user' || message.sender === 'support' ? 'justify-end' : 'justify-start'}`}
          >
            <div
              className={`max-w-[70%] rounded-lg p-3 ${
                message.sender === 'user' || message.sender === 'support'
                  ? 'bg-[#333333] text-white'
                  : 'bg-blue-100 border border-blue-200'
              }`}
            >
              {message.sender === 'driver' && (
                <div className="text-xs text-blue-600 font-medium mb-1">
                  João Silva (Condutor)
                </div>
              )}
              {message.sender === 'support' && (
                <div className="text-xs text-gray-300 font-medium mb-1">
                  Suporte A-Mover
                </div>
              )}
              <p className="text-sm">{message.text}</p>
              <p className={`text-xs mt-1 ${
                message.sender === 'user' || message.sender === 'support' 
                  ? 'text-gray-300' 
                  : 'text-blue-500'
              }`}>
                {message.timestamp}
              </p>
            </div>
          </div>
        ))}
      </div>

      <div className="flex gap-2">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Escreva uma mensagem..."
          className="flex-1 border border-gray-300 dark:border-gray-600 rounded-md p-2"
          onKeyPress={(e) => {
            if (e.key === 'Enter') {
              handleSendMessage();
            }
          }}
        />
        <button
          onClick={handleSendMessage}
          className="bg-[#333333] text-white p-2 rounded-md hover:bg-gray-700 transition-colors"
        >
          <Send size={20} />
        </button>
      </div>
    </div>
  );

  return (
    <div className="flex flex-col h-full p-4 space-y-4">
      <button
        onClick={() => setShowNewRequest(true)}
        className="bg-white dark:bg-gray-800 text-black py-2 px-4 rounded-md font-semibold shadow-md w-fit"
      >
        + Enviar Pedido
      </button>

      <div className="flex gap-4 h-full">
        {/* Left side - Request History */}
        <div className="flex-1 bg-white dark:bg-gray-800 rounded-md p-4 shadow-md overflow-auto">
          <h2 className="text-black font-semibold mb-4">Histórico de Pedidos</h2>
          <div className="space-y-2">
            {requests.map(request => (
              <div
                key={request.id}
                className="border-b border-gray-200 dark:border-gray-700 py-3 cursor-pointer hover:bg-gray-50 dark:bg-gray-700 transition-colors"
                onClick={() => setSelectedRequest(request)}
              >
                <div className="font-medium">{request.reason}</div>
                <div className="text-sm text-gray-500 dark:text-gray-400 mt-1">{request.date}</div>
                <div className="text-xs text-gray-400 mt-1">
                  {request.messages.length} mensagens
                </div>
                {request.id === 1 && (
                  <div className="mt-2">
                    <span className="px-2 py-1 bg-orange-100 text-orange-600 rounded-full text-xs font-medium">
                      Condutor: João Silva
                    </span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Right side - Chat or New Request Form */}
        <div className="flex-1">
          {selectedRequest ? (
            renderChat()
          ) : showNewRequest ? (
            <div className="bg-white dark:bg-gray-800 rounded-md p-4 shadow-md">
              <h2 className="text-black font-semibold mb-4">Novo Pedido</h2>
              <div className="space-y-4">
                <div>
                  <label className="block text-gray-700 dark:text-gray-200 mb-1">Motivo:</label>
                  <input
                    type="text"
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    className="w-full border border-gray-300 dark:border-gray-600 rounded-md p-2"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 dark:text-gray-200 mb-1">Assunto:</label>
                  <textarea
                    value={subject}
                    onChange={(e) => setSubject(e.target.value)}
                    className="w-full border border-gray-300 dark:border-gray-600 rounded-md p-2 h-32"
                  />
                </div>
                <div className="flex justify-end">
                  <button
                    onClick={handleSubmit}
                    className="bg-[#333333] text-white py-2 px-6 rounded-md font-semibold hover:bg-[#444444] transition-colors"
                  >
                    Enviar
                  </button>
                </div>
              </div>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
};

export default AssistancePage;