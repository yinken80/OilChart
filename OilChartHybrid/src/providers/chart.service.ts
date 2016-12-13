import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {OilType, ForeCaster} from '../models';

@Injectable()
export class ChartService {
    private Base_Url:string = "http://rextagpredictions.gosocialdev.eu";
    //private Base_Url:string = "http://localhost:8100/rextag";
    private GetChartData_Url = this.Base_Url + "/json_mainChart.php";
    private GetOilTypes_Url = this.Base_Url + "/API/getOilTypes.php";
    private GetForecasters_Url = this.Base_Url + "/API/getForecastersList.php";
    private GetPredictionPeriod_Url = this.Base_Url + "/API/getPredictionsPeriod.php";

    constructor(private http:Http) {
    }

    /**
     * This function is to get data of graphs and dataProvider for chart.
     */
    public getChartData(ids:string[], oilType:string, startDate:string, endDate:string) {
        let url = `${this.GetChartData_Url}?id=${ids.join(",")}`
                        +`&oiltype=${oilType}&startdate=${startDate}&enddate=${endDate}`;
        return this.get(url);
    }

    /**
     * This function is used to get oil types.
     */
    public getOilTypes() : Promise<OilType[]>{
        let url = `${this.GetOilTypes_Url}`;
        return new Promise((resolve, reject) => {
            this.get(url).then((result) => {
                let oilTypes = [];
                if (result) {
                    for(let i = 0; i < result.length; i++) {
                        let oilType = new OilType();
                        oilType.id = result[i].Id;
                        oilType.name = result[i].Name;
                        oilTypes.push(oilType);
                    }
                }
                resolve(oilTypes);
            }).catch(error => {
                reject(error);
            });       
        });
    }

    /**
     * This function is used to get forecasters.
    */
    public getForecasters() : Promise<ForeCaster[]>{
        let url = `${this.GetForecasters_Url}`;
        return new Promise((resolve, reject) => {
            this.get(url).then((result) => {
                let forecasters = [];
                if (result) {
                    for (let i = 0; i < result.length; i++) {
                        let fc = new ForeCaster();
                        fc.id = result[i].Id;
                        fc.text = result[i].ForcasterName;
                        fc.checked = (i == 0)? true:false;
                        forecasters.push(fc);
                    }
                }
                resolve(forecasters);
            }).catch(error => {
                reject(error);
            });
        });
    }

    /**
     * This function is used to get prediction period.
     */
    public getPredictionPeriod() {
        let url = `${this.GetPredictionPeriod_Url}`;
        return this.get(url);
    }

    private get(url:string, options?:any) {
        return new Promise<any>((resolve, reject) => {
            this.http.get(url, options).subscribe(response=> {
                let body = response.json();
                resolve(body);
            }, error => {
                reject("Failed to get data");
            });
        });
    }
}