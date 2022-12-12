/**
 * This method executes a search for a match between a regular expression and a specified string.
 * @param content string that you want to perform search
 * @param pattern regex pattern
 * @param flags regex flag
 * @returns Boolean -- if there is a match it returns true else false
 */
export const contains = (content:string,pattern:string,flags?:string) => {
  return new RegExp(pattern,flags).test(content);
}
