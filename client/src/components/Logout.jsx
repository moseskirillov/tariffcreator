import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { logout } from '../store/slices/user';

const events = [
  'load',
  'mousemove',
  'mousedown',
  'click',
  'scroll',
  'keypress'
];

const Logout = ({ children }) => {

  const navigate = useNavigate();
  const dispatch = useDispatch();
  let timer;

  useEffect(() => {
    Object.values(events).forEach((item) => {
      window.addEventListener(item, () => {
        resetTimer();
        handleLogoutTimer();
      });
    });
  }, []);

  const handleLogoutTimer = () => {
    const timeout = 30 * 60 * 1000;
    timer = setTimeout(() => {
      resetTimer();
      Object.values(events).forEach((item) => {
        window.removeEventListener(item, resetTimer);
      });
      logoutAction();
    }, timeout);
  };

  const resetTimer = () => {
    if (timer) {
      clearTimeout(timer);
    }
  };

  const logoutAction = () => {
    localStorage.clear();
    dispatch(logout());
    navigate('/lsc-team/tariff-creator/login');
    window.location.reload();
  };

  return children;
};

export default Logout;