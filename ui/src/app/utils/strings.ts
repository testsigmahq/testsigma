/**
 * This method will Extract a String based on the given delimeter and its occurence
 * @function
 * @param string 'www.google.com'
 * @param delimeter '.'
 * @param occurence '1' //if you want last occurence then you have to give -1
 *
 * @return string "google.com"
 *
 */
interface extractStringByDelimiterByPosArguments{
  str:string;
  delimiter?:string;
  occurence?:number;
}

export const extractStringByDelimiterByPos = ({str,delimiter=".",occurence=-1}:extractStringByDelimiterByPosArguments) => {
  return str.split(delimiter).slice(occurence).join(delimiter)
}
