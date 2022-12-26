/**
 * Builds url type of string
 * @param {string[]} commands ex - ['ui','td']
 * @returns {string} string -- ex - '/ui/td/'
 */

export const buildUrl = (commands:string[]) => {
  commands.unshift('');
  commands.push('');
  return commands.join('/')
}
