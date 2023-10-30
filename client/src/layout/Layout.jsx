import React from 'react';
import Header from './Header';
import Footer from './Footer';
import { Container } from 'react-bootstrap';

const Layout = ({children}) => {
  return (
    <>
      <Header/>
      <Container className='mt-3'>
        {children}
      </Container>
      <Footer/>
    </>
  );
};

export default Layout;