export default function replaceCityName(city) {
  if (city === 'Будденовск') {
    return 'Будденовск'
  } else if (city === 'Нефтюганск') {
    return 'Нефтеюганск'
  } else if (city === 'ростов на дону'
    || city === 'Ростов на дону'
    || city === 'ростов на Дону'
    || city === 'ростов На дону'
    || city === 'Ростов на Дону') {
    return 'Ростов-на-Дону'
  } else if (city === 'Невиномыск' || city === 'Невинномыск' || city === 'Невиномысск') {
    return 'Невинномысск'
  } else {
    return city
  }
}