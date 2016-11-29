import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Platform } from 'ionic-angular';
import { File, FileError, FileEntry, DirectoryEntry, WriteOptions, Device } from 'ionic-native';
import 'rxjs/add/operator/map';

declare var cordova: any;

@Injectable()
export class Utils {
  rootFolderPath: string;
  constructor(public http: Http, private platform: Platform) {
    platform.ready().then((value: string) => {
      let device = Device.device;
      this.rootFolderPath = this.getRootFolder(device.platform);
      console.log(this.rootFolderPath);
    });
  }

  private getRootFolder(deviceType: string) {
    let returnValue: string;
    let deviceTypeStr: string = deviceType;

    if (!this.platform.is("cordova") || !cordova || !cordova.file)
      return "";

    if (deviceTypeStr && deviceTypeStr.startsWith("BlackBerry")) deviceTypeStr = "BlackBerry";

    switch (deviceTypeStr) {
      case "iOS":
        returnValue = cordova.file.documentsDirectory;
        break;
      case "Mac OS X":
        returnValue = cordova.file.applicationStorageDirectory;
        break;
      default:
        returnValue = cordova.file.externalDataDirectory;
    }

    return returnValue;
  }

  saveFileTo(name: string, data: string | Blob) {
    let path = this.rootFolderPath + "/OilChart";
    return new Promise((resolve, reject) => {
      File.checkDir(this.rootFolderPath, "OilChart").then((isExist: FileError | boolean) => {
        console.log(isExist);
        if (typeof isExist === "FileError") {
          File.createDir(this.rootFolderPath, "OilChart", true).then((value: DirectoryEntry | FileError) => {
              this.writeToFile(path, name, data, resolve, reject);
            });
        } else {
          this.writeToFile(path, name, data, resolve, reject);
        }
      }, (reason) => {
        File.createDir(this.rootFolderPath, "OilChart", true).then((value: DirectoryEntry | FileError) => {
              this.writeToFile(path, name, data, resolve, reject);
            });
      });
    });
  }

  private writeToFile(path:string, name:string, data:string|Blob, resolve?:any, reject?:any) {
    File.writeFile(path, name, data, {replace:true}).then(() => {
      resolve(path+"/"+name);
    }).catch((reason) => {
      reject(reason);
    });
  }
}

