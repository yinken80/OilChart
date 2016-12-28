import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import { Storage } from '@ionic/storage';
import {OilType, ForeCaster, SelectOptions} from '../models';

@Injectable()
export class ChartService {
    private Base_Url:string = "https://rextag.com/OPFIndex/API";
    //private Base_Url:string = "http://localhost:8100/rextag";
    private GetChartData_Url = this.Base_Url + "/getMainChart.php";
    private GetOilTypes_Url = this.Base_Url + "/getOilTypes.php";
    private GetForecasters_Url = this.Base_Url + "/getForecastersList.php";
    private GetPredictionPeriod_Url = this.Base_Url + "/getPredictionsPeriod.php";
    private options:SelectOptions;

    constructor(private http:Http, private storage:Storage) {
    }

    /**
     * 
     */
    public setSelectOptions(options) {
        this.options = options;
    }

    public getSelectOptions() {
        return this.options;
    }

    public saveSelectOptions() {
        if (this.options) {
            this.storage.set("options", this.options);
        }        
    }

    public loadSelectOptions() {
        return new Promise<any>((resolve, reject) => {
            if (this.options) {
                resolve(this.options);
            } else {
                this.storage.get("options").then(value => {
                    if (value) {
                        this.options = value;
                        resolve(value);
                    } else {
                        this.options = null;
                        resolve(null);
                    }
                }).catch(error => {
                    reject(error);
                });
            }
        });        
    }
    /**
     * This function is to get data of graphs and dataProvider for chart.
     */
    public getChartData(ids:string[], oilType:string, startDate:string, endDate:string, isNYMEX:boolean) {
        let includeNYMEX = isNYMEX?1:0;
        let url = `${this.GetChartData_Url}?id=${ids.join(",")}`
                        +`&oiltype=${oilType}&startdate=${startDate}&enddate=${endDate}&isNYMEX=${includeNYMEX}`;
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