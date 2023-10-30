import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import tariffMap from '../../../util/tariffsPooling';
import { ruRU } from "handsontable/i18n";

const PoolingOverweight = ({hotRef, data, colHeaders}) => {
  return (
    <HotTable
      rowHeaders={true}
      ref={hotRef}
      stretchH="all"
      colHeaders={colHeaders}
      contextMenu={[]}
      renderAllRows={false}
      className="htCenter"
      data={data}
      height="60vh"
      columns={[
        {type: 'dropdown', source: Array.from(tariffMap.keys()), allowInvalid: false},
        {type: 'text', readOnly: true}
      ]}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
    />
  );
};

export default memo(PoolingOverweight);