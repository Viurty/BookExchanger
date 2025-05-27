import React from 'react';

const SuccessModal = ({ visible, message, onClose }) => {
  if (!visible) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content success">
        <h2>Успех</h2>
        <p>{message}</p>
        <button onClick={onClose}>Закрыть</button>
      </div>
    </div>

  );
};

export default SuccessModal;
