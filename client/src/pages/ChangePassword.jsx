import React, { useState } from 'react';
import MessageToast from '../components/MessageToast';
import { instance } from '../Axios';
import { useLocation, useNavigate } from 'react-router-dom';

const ResetPassword = () => {

  const search = useLocation().search;
  const token = new URLSearchParams(search).get('token');
  const navigate = useNavigate();
  const [password, setPassword] = useState('');

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('Произошла ошибка');

  const resetHandler = async () => {
    if (password === '') {
      setToastMessage('Пароль не может быть пустым');
      setShowToast(true);
    } else {
      const response = await instance.post('/auth/update', {
        token: token, password: password
      });
      if (response.status === 200) {
        setToastMessage('Пароль изменен');
        setShowToast(true);
        setTimeout(() => {
          navigate('/lsc-team/tariff-creator/login');
        }, 3000);
      } else {
        setToastMessage('Произошла ошибка');
        setShowToast(true);
      }
    }
  }

  return (
    <div className="form-signing text-center" style={{marginTop: '25vh'}}>
      <form>
        <h1 className="h3 mb-3 fw-normal">Изменить пароль</h1>
        <div className="form-floating">
          <input
            type="password"
            style={{marginBottom: '15px'}}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="form-control"
            id="floatingInput"
            placeholder="name@example.com"
          />
          <label htmlFor="floatingInput">Введите новый пароль</label>
        </div>
        <button
          onClick={resetHandler}
          className="w-100 btn btn-lg btn-primary mb-3"
          type="button"
        >
          Изменить
        </button>
      </form>
      <MessageToast
        show={showToast}
        setShow={setShowToast}
        message={toastMessage}
      />
    </div>
  );
};

export default ResetPassword;