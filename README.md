# DodgeCorona
A simple and fun to play COVID-19 pandemic themed Android game as a means to spread some much-needed awareness regarding social distancing norms and use of personal protective equipment (PPE).

The main character is trying to get home and dodges obstacles which are infected people. If the player enters the 6 feet radius (shown by green circle) of any infected person (enemy) the player gets infected and game over.

The player movement is completely controlled using the gravity accelerometer.

The player can pick a Power Up which is a medical mask for protection against infection for 5 seconds. During this time even if the player goes close to an enemy, he won’t get infected.

The difficulty of the game increases with time as the obstacle speed increases with time.

<a href='https://play.google.com/store/apps/details?id=com.project.dodgekarona'><img alt='Play Store' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height=75 width="200" />
</a>
<br/>

## Demo

![](/Screenshots/4.gif)
<br/><br/>

## Screenshots

<img src="/Screenshots/1.png" alt="drawing" width="200"/><img src="/Screenshots/2.png" alt="drawing" width="200"/><img src="/Screenshots/3.png" alt="drawing" width="200"/>
<br/><br/>
## Contributing

That said, if you liked this project and found it helpful, kindly fork/star this repo to show your support, and check out some of my [other projects](https://github.com/Ashish-Kenjale?tab=repositories). It really helps! 😁

I'm happy to accept pull requests that make DodgeCorona better. If you're thinking of contributing and want a little feedback before you jump into the codebase, post an [issue](https://github.com/Ashish-Kenjale/DodgeCorona/issues) or comment on an existing issue on Github.

## Dockerized build

To enable easy builds I have created and uploaded an image of my build environment on ![DockerHub](https://hub.docker.com/repository/docker/ashishkenjale/ashishz2020/tags?page=1). You can run a container with my image with the commands below:

```docker pull ashishkenjale/ashishz2020:android-build```

Now $cd to the app directory and run:

```docker run --rm -v "$PWD":/home/gradle/ -w /home/gradle/ ashishkenjale/ashishz2020:android-build gradle -PdisablePreDex assembleDebug```

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for details.
