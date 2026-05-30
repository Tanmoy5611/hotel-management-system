import js from '@eslint/js';
import globals from 'globals';

export default [
  js.configs.recommended,
  {
    // The frontend code runs in the browser, while webpack config runs in Node
    files: ['src/main/js/**/*.js', 'webpack.config.js'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        ...globals.browser,
        ...globals.node,
      },
    },
    rules: {
      'no-alert': 'off',
      'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
    },
  },
];
