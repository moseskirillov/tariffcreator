import { instance } from '../Axios';
import tariffMap from '../util/tariffsPooling';

export const generateLTL = (data) => {
  return instance.post('/generate', {
    type: data.type,
    commodityCode: data.commodityCode,
    clientName: data.clientName,
    email: data.email,
    dateFrom: data.dateFrom,
    dateTo: data.dateTo,
    suppliers: data.suppliers,
    ltlDelivery: data.ltlDelivery,
    ltlReturn: data.ltlReturn,
    ltlReturnType: data.ltlReturnType,
    comment: data.comment,
    surchargeOversize: data.ltlSurchargeOversize,
    standards: data.ltlStandardDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      customerWarehouse: element[0],
      warehouseFm: element[1],
      deliveryType: element[2],
      loadingUnit: element[3],
      unloadingCity: element[4],
      consigneeType: element[5],
      bodyType: element[6],
      one: element[7],
      two: element[8],
      three: element[9],
      four: element[10],
      five: element[11],
      sixEight: element[12],
      nineFifteen: element[13],
      sixteenTwenty: element[14],
      twentyOneTwentyFive: element[15]
    })),
    railway: data.ltlRailwayDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      customerWarehouse: element[0],
      warehouseFm: element[1],
      deliveryType: element[2],
      loadingUnit: element[3],
      unloadingCity: element[4],
      consigneeType: element[5],
      bodyType: element[6],
      one: element[7],
      two: element[8],
      three: element[9],
      four: element[10],
      five: element[11],
      sixEight: element[12],
      nineFifteen: element[13],
      sixteenTwenty: element[14],
      twentyOneTwentyFive: element[15]
    })),
    downtimes: data.ltlDowntimeDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      region: element[0],
      serviceType: element[1],
      time: element[2],
      tent: element[3],
      izoterm: element[4],
      ref: element[5],
    })),
    overweight: data.ltlOverweightDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      code: element[0],
      norm: tariffMap.get(element[0]),
      serviceType: element[1]
    })),
    prr: data.ltlPrrDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      serviceName: element[0],
      serviceType: element[1],
      cost: element[2]
    }))
  });
};

export const generateFTL = (data) => {
  return instance.post('/generate', {
    type: data.type,
    clientName: data.clientName,
    email: data.email,
    dateFrom: data.dateFrom,
    dateTo: data.dateTo,
    billing: data.billing,
    comment: data.comment,
    additional: data.ftlAdditionalDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      loadingLocation: element[0],
      unloadingLocation: element[1],
      vehicleType: element[2],
      serviceName: element[3],
      tent: element[4],
      izoterm: element[5],
      ref: element[6]
    })),
    intercity: data.ftlIntercityDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      departure: element[0],
      destination: element[1],
      transportType: element[2],
      tent: element[3],
      izo: element[4],
      ref: element[5]
    })),
    moscow: data.ftlMoscowDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      loadingLocation: element[0],
      truckType: element[1],
      capacity: element[2],
      bodyType: element[3],
      minimumTime: element[4],
      zoneOne: element[5],
      zoneTwo: element[6],
      zoneThree: element[7],
      zoneFour: element[8],
      downtime: element[9],
      additionalPoint: element[10],
      prr: element[11],
      forwarding: element[12]
    }))
  });
};

export const generatePooling = (data) => {
  return instance.post('/generate', {
    type: data.type,
    commodityCode: data.commodityCode,
    clientName: data.clientName,
    email: data.email,
    dateFrom: data.dateFrom,
    dateTo: data.dateTo,
    suppliers: data.suppliers,
    comment: data.comment,
    standard: data.poolingDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      customerWarehouse: element[0],
      warehouseFm: element[1],
      delivery: element[2],
      loadingUnit: element[3],
      unloadingCity: element[4],
      bodyType: element[5],
      oneFifteen: element[6],
      sixteenTwenty: element[7],
      twentyOneTwentyFive: element[8],
      twentySix: element[9],
      twentySeven: element[10],
      twentyEight: element[11],
      twentyNine: element[12],
      thirty: element[13],
      thirtyOne: element[14],
      thirtyTwo: element[15],
      thirtyThree: element[16]
    })),
    downtimes: data.poolingDowntimeDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      region: element[0],
      serviceType: element[1],
      time: element[2],
      tent: element[3],
      izoterm: element[4],
      ref: element[5]
    })),
    overweight: data.poolingOverweightDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      code: element[0],
      norm: tariffMap.get(element[0]),
      serviceType: element[1]
    })),
    additional: data.poolingAdditionalDataRef
    .current
    .hotInstance
    .getData()
    .map(element => ({
      loadingLocation: element[0],
      consignee: element[1],
      cargoType: element[2],
      deliveryCost: element[4],
      customerDiscount: element[5],
      shippingFee: element[6],
      fenceFee: element[7]
    }))
  });
};