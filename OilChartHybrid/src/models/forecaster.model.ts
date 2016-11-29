export interface IForeCaster {
    text: string;
}

export class ForeCaster implements IForeCaster{
    text: string;
    checked: boolean;
    constructor() {
        this.checked = false;
        this.text = "";
    }
}