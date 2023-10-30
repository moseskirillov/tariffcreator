export const poolingDataUtil = () => {
  return [
    ['', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '']
  ];
}

export const poolingNestedHeaders = [
  [
    `Склад</br> заказчика`,
    'Склад FM',
    'Доставка',
    `Грузовая</br> единица`,
    `Город</br> выгрузки`,
    'Тип кузова',
    {
      label: `Стоимость доставки 1 паллетоместа при следующем количестве</br> паллет в поставке, руб. без НДС и без копеек`,
      colspan: 11
    }
  ], ['', '', '', '', '', '', '1-15', '16-20', '21-25', '26', '27', '28', '29', '30', '31', '32', '33']
];

export const poolingDowntimeDataUtil = () => {
  return [
    ['Москва и МО', 'PL', '12', '', '', ''],
    ['Москва и МО', 'PL', '24', '', '', ''],
    ['Регионы', 'PL', '24', '', '', ''],
    ['Москва и МО', 'PF', '12', '', '', ''],
    ['Москва и МО', 'PF', '24', '', '', ''],
    ['Регионы', 'PF', '24', '', ''],
    ['Москва и МО', 'LD', '12', '', '', ''],
    ['Москва и МО', 'LD', '24', '', '', ''],
    ['Регионы', 'LD', '24', '', '']
  ];
}

export const poolingDowntimeNestedHeaders = [
  [
    'Простои в',
    'Тип услуги',
    `Время начала<br/> платного простоя`,
    {
      label: 'Стоимость за паллет/сутки, руб. без НДС',
      colspan: 3
    }
  ], ['', '', '', 'Тент', 'Изотерм', 'Реф']
];

export const poolingDowntimeColumns = [
  {type: 'dropdown', source: ['Москва и МО', 'Регионы']},
  {type: 'dropdown', source: ['PL', 'PF', 'LD']},
  {type: 'dropdown', source: ['12', '24']},
  {type: 'numeric'},
  {type: 'numeric'},
  {type: 'numeric'}
];

export const poolingOverweightDataUtil = () => {
  return [
    ['', 'PL'], ['', 'PF'], ['', 'LD']
  ];
}

export const poolingAdditionalDataUtil = () => {
  return [
    ['', '', '', '', '', '', '', '']
  ];
}

export const poolingOverweightColHeaders = ['Норма веса', 'Тип услуги'];

export const poolingAdditionalColHeaders = [
  'Место загрузки',
  'Грузополучатель',
  'Тип груза',
  'Тоннажность',
  'Стоимость доставки FTL',
  'Скидка заказчику FTL',
  'Тариф за доставку LTL',
  'Тариф за забор'
];