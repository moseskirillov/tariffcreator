import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { cities } from '../../../util/tariffsPooling';
import { ruRU } from 'handsontable/i18n';
import replaceCityName from '../../../util/functions';

const LtlMain = (
  {
    hotRef,
    data,
    nestedHeaders,
    warehouses,
    searchNames
  }) => {
  return (
    <HotTable
      ref={hotRef}
      colHeaders={true}
      contextMenu={true}
      rowHeaders={true}
      data={data}
      height="60vh"
      nestedHeaders={nestedHeaders}
      renderAllRows={false}
      columns={[
        { type: 'dropdown', source: warehouses, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: searchNames, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['LT', 'MG'], allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['EP', 'LP', 'HP', 'OT'], allowInvalid: true, validator: 'dropdown' },
        {
          type: 'dropdown',
          source: [...cities, 'Березники', 'Калининград'].sort(),
          allowInvalid: true,
          validator: 'dropdown'
        },
        { type: 'dropdown', source: ['сети', 'другое'], allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['реф', 'изо', 'тент'], allowInvalid: true, validator: 'dropdown' },
        { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }
      ]}
      colWidths={[120, 120, 60, 60, 100, 80, 80, 50, 50, 50, 50, 50, 50, 50, 50, 50]}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 7
            || changes[i][1] === 8
            || changes[i][1] === 9
            || changes[i][1] === 10
            || changes[i][1] === 11
            || changes[i][1] === 12
            || changes[i][1] === 13
            || changes[i][1] === 14
            || changes[i][1] === 15) {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].replace(/\s/g, '');
            }
          } else if (changes[i][1] === 5 || changes[i][1] === 6) {
            changes[i][3] = changes[i][3].toLowerCase().trim();
          } else if (changes[i][1] === 4) {
            changes[i][3] = replaceCityName(changes[i][3]);
          } else {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].trim();
            }
          }
        }
      }}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
    />
  );
};


export default memo(LtlMain);