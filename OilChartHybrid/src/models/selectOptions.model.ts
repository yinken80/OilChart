import {IForeCaster, ForeCaster} from './forecaster.model';
import {OilType} from './oiltype.model';

export interface Range {
    lower : number;
    upper : number;
}
export class SelectOptions {
    oilTypes:OilType[];
    oilType: string;
    forecasters: ForeCaster[];
    dateRange: Range;
    constructor() {
        this.oilTypes = [];
        this.oilType = "0";
        this.forecasters = [];
        this.dateRange = {
            lower: 0,
            upper: 100
        };
    }
}