import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { cities } from '../../../util/tariffsPooling';
import { ruRU } from 'handsontable/i18n';
import replaceCityName from '../../../util/functions';

const PoolingMain = ({ hotRef, data, nestedHeaders, warehouses, searchNames }) => {
  return (
    <HotTable
      rowHeaders={true}
      ref={hotRef}
      stretchH="all"
      colHeaders={true}
      contextMenu={true}
      className="htCenter"
      data={data}
      height="60vh"
      nestedHeaders={nestedHeaders}
      columns={[
        { type: 'dropdown', source: warehouses, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: searchNames, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['PL', 'PF', 'PL = LT retail'], allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['EP', 'LP', 'HP', 'OT'], allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: cities, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['реф', 'тент'], allowInvalid: true, validator: 'dropdown' },
        { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }
      ]}
      colWidths={[150, 90, 100, 50, 120, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30]}
      renderAllRows={false}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 6
            || changes[i][1] === 7
            || changes[i][1] === 8
            || changes[i][1] === 9
            || changes[i][1] === 10
            || changes[i][1] === 11
            || changes[i][1] === 12
            || changes[i][1] === 13
            || changes[i][1] === 14
            || changes[i][1] === 15
            || changes[i][1] === 16) {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].replace(/\s/g, '');
            }
          } else if (changes[i][1] === 4) {
            changes[i][3] = replaceCityName(changes[i][3]);
          } else if (changes[i][1] === 5) {
            changes[i][3] = changes[i][3].toLowerCase().trim();
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

export default memo(PoolingMain);