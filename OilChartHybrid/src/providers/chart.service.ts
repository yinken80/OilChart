import {Injectable} from '@angular/core';
import {Http} from '@angular/http';

@Injectable()
export class ChartService {
    //private Base_Url:string = "http://rextagpredictions.gosocialdev.eu/json_mainChart.php";
    private Base_Url:string = "http://localhost:8100/rextag/json_mainChart.php";
    constructor(private http:Http) {
    }

    /**
     * This function is to get data of graphs and dataProvider for chart.
     */
    public getChartData(ids:string[], oilType:number, startDate:string, endDate:string) {
        let url = `${this.Base_Url}?id=${ids.join(",")}`
                        +`&oilType=${oilType}&startDate=${startDate}&endDate=${endDate}`;
        return new Promise<any>((resolve, reject) => {
            this.http.get(url).subscribe(response=> {
                let body = response.json();
                resolve(body);
            },error => {
                reject("Failed to get chart data");
            });
        });
    }
}