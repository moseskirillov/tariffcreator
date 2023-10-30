import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { login } from '../store/slices/user';
import MessageToast from '../components/MessageToast';
import { useNavigate } from 'react-router-dom';
import { instance } from '../Axios';

const Login = () => {

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('Произошла ошибка');

  const loginHandler = async () => {
    try {
      const response = await instance.post('/auth/login', {
        email: email, password: password
      });
      dispatch(login({
        email: response.data.email,
        roles: response.data.roles,
        accessToken: response.data.accessToken,
        refreshToken: response.data.refreshToken
      }));
      navigate('/lsc-team/tariff-creator')
    } catch (error) {
      console.error(error);
      if (error.response.status === 401) {
        setToastMessage('Неверный логин или пароль!');
        setShowToast(true);
      } else {
        setToastMessage('Произошла ошибка');
        setShowToast(true);
      }
    }
  }

  return (
    <div className="form-signing text-center" style={{marginTop: '25vh'}}>
      <form>
        <h1 className="h3 mb-3 fw-normal">Форма входа</h1>
        <div className="form-floating">
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="form-control"
            id="floatingInput"
            placeholder="name@example.com"
          />
          <label htmlFor="floatingInput">Email</label>
        </div>
        <div className="form-floating" style={{marginBottom: '1rem'}}>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="form-control"
            id="floatingPassword"
            placeholder="Password"
          />
          <label htmlFor="floatingPassword">Пароль</label>
        </div>
        <button
          onClick={loginHandler}
          className="w-100 btn btn-lg btn-primary mb-3"
          type="button"
        >
          Войти
        </button>
        <button
          onClick={() => navigate('/lsc-team/tariff-creator/reset')}
          className="w-100 btn btn-lg btn-outline-secondary mb-3"
          type="button"
        >
          Забыли пароль?
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

export default Login;