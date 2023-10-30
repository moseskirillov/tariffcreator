import React, { useState } from 'react';
import MessageToast from '../components/MessageToast';
import { instance } from '../Axios';

const ResetPassword = () => {

  const [email, setEmail] = useState('');

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('Произошла ошибка');

  const resetHandler = async () => {
    if (email === '') {
      setToastMessage('Введите правильный email');
      setShowToast(true);
    } else {
      const response = await instance.post('/auth/reset', {
        email: email
      });
      if (response.status === 200) {
        setToastMessage('На вашу почту направлено письмо со ссылокой для сброса пароля');
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
        <h1 className="h3 mb-3 fw-normal">Восстановить пароль</h1>
        <div className="form-floating">
          <input
            type="email"
            style={{marginBottom: '15px'}}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="form-control"
            id="floatingInput"
            placeholder="name@example.com"
          />
          <label htmlFor="floatingInput">Введите ваш email</label>
        </div>
        <button
          onClick={resetHandler}
          className="w-100 btn btn-lg btn-primary mb-3"
          type="button"
        >
          Восстановить
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