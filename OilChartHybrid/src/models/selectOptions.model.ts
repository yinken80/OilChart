import {IForeCaster, ForeCaster} from './forecaster.model';
import {OilType} from './oiltype.model';

export interface Range {
    lower : string;
    upper : string;
}
export class SelectOptions {
    oilTypes:OilType[];
    oilType: number;
    forecasters: ForeCaster[];
    dateRange: Range;
    isNYMEX:boolean;
    constructor() {
        this.oilTypes = [];
        this.oilType = 0;
        this.forecasters = [];
        this.dateRange = {
            lower: "1970-01-01",
            upper: "2020-12-12"
        };
        this.isNYMEX = true;
    }
}