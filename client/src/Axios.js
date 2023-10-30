import axios from 'axios';

const instance = axios.create({
  baseURL: 'https://k8s-ru.fmlogistic.com/lsc-team/tariff-creator/api'
})

export { instance }