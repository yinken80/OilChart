import {IForeCaster, ForeCaster} from './forecaster.model';

export interface Range {
    lower : number;
    upper : number;
}
export class SelectOptions {
    oilType: number;
    forecasters: ForeCaster[];
    dateRange: Range;
    constructor() {
        this.oilType = 0;
        this.forecasters = [];
        this.dateRange = {
            lower: 0,
            upper: 0
        };
    }
}