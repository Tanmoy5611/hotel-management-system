import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const jsDirectory = path.resolve(__dirname, 'src/main/js');

// Only top-level files become webpack page entries
const entries = fs
  .readdirSync(jsDirectory)
  .filter((file) => file.endsWith('.js'))
  .reduce((entryMap, file) => {
    const name = path.basename(file, '.js');
    entryMap[name] = path.resolve(jsDirectory, file);
    return entryMap;
  }, {});

export default {
  mode: 'development',
  entry: entries,
  output: {
    filename: 'js/bundle-[name].js',
    path: path.resolve(__dirname, 'src/main/resources/static'),
  },
  devtool: 'source-map',
  module: {
    rules: [
      {
        // SCSS is compiled and extracted as normal Spring static CSS
        test: /\.s[ac]ss$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader', 'sass-loader'],
      },
      {
        // Plain CSS imports from npm packages are supported too
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader'],
      },
      {
        // Bootstrap Icons font files are copied beside the generated bundles
        test: /\.(woff2?|ttf|eot|svg)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'fonts/[name][ext]',
        },
      },
    ],
  },
  plugins: [
    new MiniCssExtractPlugin({
      // Each JavaScript entry may produce its own matching stylesheet
      filename: 'css/bundle-[name].css',
    }),
  ],
};
