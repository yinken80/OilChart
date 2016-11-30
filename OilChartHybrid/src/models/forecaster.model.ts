export interface IForeCaster {
    text: string;
    id: string;
}

export class ForeCaster implements IForeCaster{
    text: string;
    id: string;
    checked: boolean;
    constructor() {
        this.checked = false;
        this.text = "";
    }
}