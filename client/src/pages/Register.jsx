import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MessageToast from '../components/MessageToast';
import { instance } from '../Axios';
import { useFormik } from "formik";
import { object, ref, string } from 'yup';

const Register = () => {

  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordRepeat, setPasswordRepeat] = useState('');

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('Произошла ошибка');

  const registerHandler = async () => {
    try {
      const response = await instance.post('/auth/register', {
        email: email, password: password
      });
      if (response.status === 200) {
        setToastMessage('Регистрация прошла успешно!');
        setShowToast(true);
        setTimeout(() => {
          navigate('/lsc-team/tariff-creator/login');
        }, 3000);
      }
    } catch (error) {
      if (error.response.status === 409) {
        setToastMessage('Такой пользователь уже существует!');
        setShowToast(true);
      } else {
        setToastMessage('Произошла ошибка!');
        setShowToast(true);
      }
      formik.resetForm();
    }
  };

  const schema = object().shape({
    email: string().email('Невалидный email').required('Обязательное поле'),
    password: string().min(5, 'Минимум 5 символов').required('Обязательное поле'),
    passwordRepeat: string().min(5, 'Минимум 5 символов').oneOf([ref('password'), null], 'Пароли не совпадают').required('Обязательное поле'),
  });

  const formik = useFormik({
    initialValues: {
      email: '',
      password: '',
      passwordRepeat: ''
    },
    validationSchema: schema,
    onSubmit: async () => {
      await registerHandler();
      formik.resetForm();
    }
  });

  return (
    <div className="form-signing text-center" style={{marginTop: '25vh'}}>
      <form onSubmit={formik.handleSubmit}>
        <h1 className="h3 mb-3 fw-normal">Форма регистрации</h1>
        <div className="form-floating">
          <input
            type="email"
            value={formik.values.email}
            onChange={(e) => {
              setEmail(e.target.value)
              formik.handleChange(e);
            }}
            className="form-control"
            id="email"
            placeholder="name@example.com"
          />
          <label htmlFor="floatingInput">Email</label>
          {formik.errors.email ?
            <p style={{
              color: 'red',
              marginTop: '5px',
              marginBottom: '0'
            }}>{formik.errors.email}</p> : null}
        </div>
        <div className="form-floating">
          <input
            style={{marginBottom: '-1px'}}
            type="password"
            value={formik.values.password}
            onChange={(e) => {
              setPassword(e.target.value)
              formik.handleChange(e);
            }}
            className="form-control"
            id="password"
            placeholder="Password"
          />
          <label htmlFor="floatingInput">Пароль</label>
          {formik.errors.password ?
            <p style={{
              color: 'red',
              marginTop: '5px',
              marginBottom: '0'
            }}>{formik.errors.password}</p> : null}
        </div>
        <div className="form-floating" style={{marginBottom: '1rem'}}>
          <input
            type="password"
            value={formik.values.passwordRepeat}
            onChange={(e) => {
              setPasswordRepeat(e.target.value)
              formik.handleChange(e);
            }}
            className="form-control"
            id="passwordRepeat"
            placeholder="Password"
          />
          <label htmlFor="floatingPassword">Повторите пароль</label>
          {formik.errors.passwordRepeat ?
            <p style={{
              color: 'red',
              marginTop: '5px',
              marginBottom: '0'
            }}>{formik.errors.passwordRepeat}</p> : null}
        </div>
        <button
          className="w-100 btn btn-lg btn-primary mb-3"
          type="submit"
        >
          Зарегистрироваться
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

export default Register;