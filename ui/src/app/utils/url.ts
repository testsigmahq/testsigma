/**
 * Builds url type of string
 * @param {string[]} commands ex - ['ui','td']
 * @returns {string} string -- ex - '/ui/td/'
 */

export const buildUrl = (commands:string[]) => {
  return "/"+commands.join('/')+"/"
}
