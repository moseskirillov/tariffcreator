import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../store/slices/user';

const Header = () => {

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const {isAuth, admin, email} = useSelector(state => state.user);

  return (
    <header className="p-3 text-bg-primary">
      <div className="container">
        <div className="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
          <NavLink to="/lsc-team/tariff-creator"
                   className="d-flex align-items-center mb-3 mb-lg-0 me-lg-3 text-white text-decoration-none">
            <span className="fs-4">Tariff Creator</span></NavLink>
          {isAuth && <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-end mb-md-0">
            <li>
              <NavLink to="/lsc-team/tariff-creator/ltl" className="nav-link px-2 text-white">LTL</NavLink>
            </li>
            <li>
              <NavLink to="/lsc-team/tariff-creator/ftl" className="nav-link px-2 text-white">FTL</NavLink>
            </li>
            <li>
              <NavLink to="/lsc-team/tariff-creator/pooling" className="nav-link px-2 text-white">Pooling</NavLink>
            </li>
          </ul>}
          {!isAuth && <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-end mb-md-0"></ul>}
          {isAuth && <h6 className="me-3 mb-0">{email}</h6>}
          <div className="text-end">
            {(isAuth && admin) && <button type="button" onClick={() => navigate('/lsc-team/tariff-creator/validation')}
                                          className="btn btn-warning me-2">Валидация</button>}
            {!isAuth && <button type="button" onClick={() => navigate('/lsc-team/tariff-creator/login')}
                                className="btn btn-outline-light me-2">Вход</button>}
            {isAuth &&
              <button type="button" onClick={() => {
                dispatch(logout());
                navigate('/lsc-team/tariff-creator/login')
              }} className="btn btn-outline-light me-2">Выйти
              </button>}
            {!isAuth && <button type="button" onClick={() => navigate('/lsc-team/tariff-creator/register')} className="btn btn-warning">Регистрация</button>}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;