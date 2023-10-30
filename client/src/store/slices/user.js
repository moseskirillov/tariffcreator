import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  email: sessionStorage.getItem('user-email') || '',
  admin: sessionStorage.getItem('user-admin') || false,
  accessToken: sessionStorage.getItem('user-accessToken') || '',
  refreshToken: localStorage.getItem('user-refreshToken') || '',
  isAuth: localStorage.getItem('user-is-auth') || false
};

const slice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    login(state, action) {
      state.email = action.payload.email
      sessionStorage.setItem('user-email', action.payload.email)
      state.admin = checkAdmin(action.payload.roles)
      sessionStorage.setItem('user-admin', String(checkAdmin(action.payload.roles)))
      state.accessToken = action.payload.accessToken
      sessionStorage.setItem('user-accessToken', action.payload.accessToken)
      state.refreshToken = action.payload.refreshToken
      localStorage.setItem('user-refreshToken', action.payload.refreshToken)
      state.isAuth = true
      localStorage.setItem('user-is-auth', String(true))
    },
    logout(state) {
      state.email = null
      state.admin = false
      state.accessToken = null
      state.refreshToken = null
      state.isAuth = false
      sessionStorage.clear();
      localStorage.removeItem('user-refreshToken');
      localStorage.removeItem('user-is-auth');
    }
  }
});

const checkAdmin = (roles) => {
  for (const role of roles) {
    if (role === 'ROLE_ADMIN') {
      return true
    }
  }
  return false
}

export const { login, logout } = slice.actions
export default slice.reducer